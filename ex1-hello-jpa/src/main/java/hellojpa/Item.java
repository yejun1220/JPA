package hellojpa;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // DTYPE 상속 테이블 생성
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 단일 테이블 생성
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) // 부모 테이블 생성 X, 추상 클래스로 만들어야 함(안 만들 시 객체 하나로 봄)
@DiscriminatorColumn
public class Item {

    @Id @GeneratedValue
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    private int price;
}
