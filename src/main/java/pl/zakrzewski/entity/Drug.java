package pl.zakrzewski.entity;

import pl.zakrzewski.DrugType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "drugs")
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "prod_year")
    private LocalDate prodYear;
    private BigDecimal price;

    @Transient
    private long age;

    @Enumerated(EnumType.STRING)
    private DrugType drugType;

    @PostLoad
    public void calculateAge() {
        age = ChronoUnit.YEARS.between(prodYear, LocalDate.now());
    }

    public DrugType getDrugType() {
        return drugType;
    }

    public void setDrugType(DrugType drugType) {
        this.drugType = drugType;
    }

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

    public LocalDate getProdYear() {
        return prodYear;
    }

    public void setProdYear(LocalDate prodYear) {
        this.prodYear = prodYear;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Drug{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", prodYear=" + prodYear +
                ", price=" + price +
                ", age=" + age +
                ", drugType=" + drugType +
                '}';
    }

}
