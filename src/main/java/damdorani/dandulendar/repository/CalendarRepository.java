package damdorani.dandulendar.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import damdorani.dandulendar.domain.Calendar;
import damdorani.dandulendar.domain.CalendarDetail;
import static damdorani.dandulendar.domain.QCalendar.calendar;
import static damdorani.dandulendar.domain.QCalendarDetail.calendarDetail;

import damdorani.dandulendar.dto.CalendarRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CalendarRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public void saveCalendar(Calendar calendar){
        em.persist(calendar);
    }

    public Calendar findCalendar(int id){
        return em.find(Calendar.class, id);
    }

    public List<Calendar> findCalendarList(int groupId){
        return queryFactory
                .selectDistinct(calendar)
                .from(calendar)
                .where(calendar.del_yn.eq("N")
                        .and(calendar.group.group_id.eq(groupId))
                )
                .fetch();
//        return em.createQuery("select c from Calendar c where del_yn = 'N'", Calendar.class).getResultList();
    }

    public void saveCalendarDetail(CalendarDetail calendarDetail){
        em.persist(calendarDetail);
    }

    public CalendarDetail findCalendarDetail(int calDtlId) {
        return em.find(CalendarDetail.class, calDtlId);
    }

    public List<Calendar> findCalendarDetailList(CalendarRequest calendarRequest) {
        return queryFactory
                .selectDistinct(calendar)
                .from(calendar)
                .join(calendar.calendars, calendarDetail)
                .where(
                        calendarDetail.del_yn.eq("N")
                                .and(calendar.group.group_id.eq(calendarRequest.getGroupId()))
                        .and(calendarDetail.start_full_date.between(calendarRequest.getStartStr(), calendarRequest.getEndStr()))
                        .and(calendarDetail.end_full_date.between(calendarRequest.getStartStr(), calendarRequest.getEndStr()))
                )
                .fetchJoin()
                .fetch();
    }
}
