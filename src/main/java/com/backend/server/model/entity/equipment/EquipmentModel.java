package com.backend.server.model.entity.equipment;

import com.backend.server.model.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "equipment_model")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EquipmentModel extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String englishCode;

    private boolean available;

    @Column(name = "model_group_index")
    private Integer modelGroupIndex;  //시리얼넘버 중복 방지를 위해 모델별 인덱스

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private EquipmentCategory category;

    @OneToMany(mappedBy = "equipmentModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Equipment> equipments;

    @Override
    public void softDelete(){
        super.softDelete();
        if(equipments != null){
            for (Equipment equipment : equipments){
                equipment.softDelete();
            }
        }
    }
}