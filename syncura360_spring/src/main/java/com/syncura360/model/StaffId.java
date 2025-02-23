//package com.example.syncura360_spring.model;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import org.springframework.data.util.ProxyUtils;
//
//import java.util.Objects;
//
//@Data
//@Embeddable
//public class StaffId implements java.io.Serializable {
//    private static final long serialVersionUID = -7398223966449890386L;
//
//    @Column(name = "staff_id", nullable = false)
//    private Integer staffId;
//
//    @JoinColumn(name = "works_at", nullable = false)
//    private Integer worksAt;
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || ProxyUtils.getUserClass(this) != ProxyUtils.getUserClass(o)) return false;
//        StaffId entity = (StaffId) o;
//        return Objects.equals(this.worksAt, entity.worksAt) &&
//                Objects.equals(this.staffId, entity.staffId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(worksAt, staffId);
//    }
//
//}