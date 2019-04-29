package org.crossfit.app.repository;

import java.util.List;
import java.util.Set;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.Subscription;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the Subscription entity.
 */
public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {

    @Query("select s from Subscription s  "
    		+ "left join fetch s.membership ms "
    		+ "left join fetch ms.membershipRules msr "
    		+ "left join fetch msr.applyForTimeSlotTypes "
    		+ "left join fetch s.directDebit dd "
    		+ "left join fetch dd.mandate m "
    		+ "left join fetch s.contractModel cm "
    		+ "where s.member = :member")
	Set<Subscription> findAllByMember(@Param("member") Member member);
    
    @Query("select s from Subscription s  "
    		+ "left join fetch s.membership ms "
    		+ "left join fetch ms.membershipRules msr "
    		+ "left join fetch msr.applyForTimeSlotTypes "
    		+ "where s.id = :id")
	Subscription findOneWithRules(@Param("id") Long id);

	List<Subscription> findAllByMembership(Membership membership);
   
	
	 @Query("select s from Subscription s "
	 		+ "join s.membership ms "
	 		+ "join s.member m "
	 		+ "where m.box = :box "
	    		+ "and ( "
	    		+ "	lower(m.firstName) like :search "
	    		+ "	or lower(m.lastName) like :search "
	    		+ "	or lower(m.telephonNumber) like :search "
	    		+ "	or lower(m.login) like :search "
	    		+ ") "
	    		+ "order by s.subscriptionEndDate desc, m.lastName, m.firstName")
	Page<Subscription> findAllSubscriptionOfMemberLike(@Param("box") CrossFitBox box, @Param("search") String search, Pageable pageable);

	 
	 @Query("select s from Subscription s "
		 		+ "join fetch s.membership ms "
	    		+ "left join fetch ms.membershipRules msr "
		 		+ "join fetch s.member m "
		 		+ "where ms.box = :box")
	 Set<Subscription> findAllByBoxWithMembership(@Param("box") CrossFitBox box);

	 @Query("select s from Subscription s "
	 		+ "join fetch s.membership ms "
	 		+ "join fetch s.member m "
	 		+ "where ms.box = :box "
	 		+ "AND ( (s.subscriptionStartDate <= :at AND :at < s.subscriptionEndDate) OR (s.subscriptionStartDate <= :at2 AND :at2 < s.subscriptionEndDate) )")
	 Set<Subscription> findAllByBoxAtDateOrAtDate(@Param("box") CrossFitBox box, @Param("at")  LocalDate at, @Param("at2")  LocalDate orAt);

}
