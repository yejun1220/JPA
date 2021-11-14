package jpabook.jpashop;

import jpabook.jpashop.domain.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            // JPQL
            List<Member> resultList = em.createQuery("select m From Member m where m.name like '%kim%'", Member.class).getResultList();

            // Criteria
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> query = cb.createQuery(Member.class);

            Root<Member> m = query.from(Member.class);
            CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("name"), "kim"));
            List<Member> resultList2 = em.createQuery(cq).getResultList();

            // QueryDSL
//            JPAFactoryQuery query = new JPAQueryFactory(em);
//            QMember m = QMember.member;
//            List<Member> list =
//                    query.selectFrom(m)
//                         .where(m.age.gt(18))
//                         .orderBy(m.name.desc())
//                         .fetch();

            // 네이티브 쿼리
            em.createNativeQuery("select MEMBER_ID, city, street, zipcode, NAME from MEMBER").getResultList();

            Member member = new Member();
            member.setName("member1");
            em.persist(member); // DB에 데이터 아직 안들어감.

            List<Member> resultList3 = em.createNativeQuery("select MEMBER_ID, city, street, zipcode, NAME from MEMBER", Member.class).getResultList();

            for (Member member1 : resultList3) {
                System.out.println("member1 = " + member1.getName());
            }
            // flush -> commit, query 날라갈 때 실행

            tx.commit();
        }
        catch (Exception e) {
            System.out.println("e = " + e);
            tx.rollback();
        }
        finally {
            em.close();
        }

        emf.close();
    }
}

