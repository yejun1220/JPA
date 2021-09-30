package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        Member member = new Member();
        member.setId("member1");
        member.setUsername("회원1");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        //객체를 저장한 상태(영속)
        em.persist(member);

        em.close();

        emf.close();
    }
}

