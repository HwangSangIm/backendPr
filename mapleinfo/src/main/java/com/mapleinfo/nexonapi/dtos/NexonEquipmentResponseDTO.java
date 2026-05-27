package com.mapleinfo.nexonapi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NexonEquipmentResponseDTO {

    @JsonProperty("character_class")
    private String characterClass;

    @JsonProperty("item_equipment")
    private List<ItemEquipmentDTO> itemEquipment;

    @Data
    public static class ItemEquipmentDTO {
        @JsonProperty("item_equipment_slot")
        private String itemEquipmentSlot;

        @JsonProperty("item_name")
        private String itemName;

        @JsonProperty("item_icon")
        private String itemIcon;

        @JsonProperty("potential_option_grade")
        private String potentialOptionGrade;

        @JsonProperty("starforce")
        private String starforce;

        @JsonProperty("scroll_upgrade")
        private String scrollUpgrade;

        @JsonProperty("item_total_option")
        private ItemOptionDTO itemTotalOption;

        @JsonProperty("item_base_option")
        private ItemOptionDTO itemBaseOption;

        @JsonProperty("item_add_option")
        private ItemOptionDTO itemAddOption;

        @JsonProperty("item_etc_option")
        private ItemOptionDTO itemEtcOption;

        @JsonProperty("item_starforce_option")
        private ItemOptionDTO itemStarforceOption;

        @JsonProperty("potential_option_1")
        private String potentialOption1;
        @JsonProperty("potential_option_2")
        private String potentialOption2;
        @JsonProperty("potential_option_3")
        private String potentialOption3;

        @JsonProperty("additional_potential_option_1")
        private String additionalPotentialOption1;
        @JsonProperty("additional_potential_option_2")
        private String additionalPotentialOption2;
        @JsonProperty("additional_potential_option_3")
        private String additionalPotentialOption3;

        @JsonProperty("soul_name")
        private String soulName;
        @JsonProperty("soul_option")
        private String soulOption;
    }

    @Data
    public static class ItemOptionDTO {
        private String str;
        private String dex;

        @JsonProperty("int")
        private String intel;

        private String luk;

        @JsonProperty("max_hp")
        private String maxHp;

        @JsonProperty("max_mp")
        private String maxMp;

        @JsonProperty("attack_power")
        private String attackPower;

        @JsonProperty("magic_power")
        private String magicPower;

        private String defense;
        private String speed;
        private String jump;

        @JsonProperty("all_stat")
        private String allStat;

        @JsonProperty("boss_damage")
        private String bossDamage;

        @JsonProperty("ignore_target_defense")
        private String ignoreTargetDefense;

        private String damage;

        @JsonProperty("equipment_level_decrease")
        private String equipmentLevelDecrease;
    }
}