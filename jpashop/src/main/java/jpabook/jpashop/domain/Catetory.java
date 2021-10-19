package jpabook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Catetory {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID") // 매핑할 외래키
    private Catetory parent;

    @OneToMany(mappedBy = "parent")
    private List<Catetory> child = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "CATEGORY_ITEM", // 조인 테이블 명
               joinColumns = @JoinColumn(name = "CATEGORY_ID"), // 외래키
               inverseJoinColumns = @JoinColumn(name = "ITEM_ID") // 반대 엔티티의 외래키
    )
    private List<Item> items = new ArrayList<>();

}
