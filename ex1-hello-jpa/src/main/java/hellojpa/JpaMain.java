package hellojpa;

import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member1 = new Member();
            member1.setUsername("Member1");
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("Member2");
            em.persist(member2);

            em.flush();
            em.clear();

            Member m1 = em.find(Member.class, member1.getId());
            System.out.println("m1 = " + m1.getClass());

            Member reference = em.getReference(Member.class, member2.getId());
            System.out.println("reference = " + reference.getClass());
            System.out.println("a == a: " + (m1 == reference));
            System.out.println("isLoad = " + emf.getPersistenceUnitUtil().isLoaded(reference));
            Hibernate.initialize(reference); // 강제 프록시 초기화
            System.out.println("isLoad = " + emf.getPersistenceUnitUtil().isLoaded(reference));
//            Member findMember = em.getReference(Member.class, member.getId()); // 실제 사용될 때 insert문이 나간다.
//            System.out.println("findMember.username = " + findMember.getUsername()); // 사용 전에는 가짜 proxy를 반환해주고 사용할 때 target을 지정한다.

            em.clear();
            em.close();
            em.detach(reference);

            reference.getUsername();

            tx.commit();
        }
        catch (Exception e) {
            tx.rollback();
            System.out.println(e);
        }
        finally {
            em.close();
        }

        emf.close();
    }

    private static void logic(Member m1, Member m2) {
        System.out.println("m1 == m2 = " + (m1 instanceof Member));
        System.out.println("m1 == m2 = " + (m2 instanceof Member));
    }
}

