package es.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EsAddressEntity implements Serializable {

    private Long id;

    private Long parentId;

    private String code;

    private String shorthandCode;

    private String name;

    private String namePinyin;

    private String fullName;

    private Integer level;

    private String lastThreeValue;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Integer lastThreeUnit;

    private String path;

    private String location;

    private String polygon;

    private String remark;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Integer addrState;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Long provinceId;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Long cityId;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Long countyId;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Long townId;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Long villageId;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Long streetId;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Long groupId;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Long communityId;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Long buildingId;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Long unitId;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Long floorId;

    @JsonSerialize(nullsUsing = NullSerializer.class)
    private Long roomId;

    public EsAddressEntity() {
    }

    public EsAddressEntity(Long id, Long parentId, String code, String shorthandCode, String name, String namePinyin, String fullName, Integer level, String lastThreeValue, Integer lastThreeUnit, String path, String location, String polygon, String remark, Integer addrState, Long provinceId, Long cityId, Long countyId, Long townId, Long villageId, Long streetId, Long groupId, Long communityId, Long buildingId, Long unitId, Long floorId, Long roomId) {
        this.id = id;
        this.parentId = parentId;
        this.code = code;
        this.shorthandCode = shorthandCode;
        this.name = name;
        this.namePinyin = namePinyin;
        this.fullName = fullName;
        this.level = level;
        this.lastThreeValue = lastThreeValue;
        this.lastThreeUnit = lastThreeUnit;
        this.path = path;
        this.location = location;
        this.polygon = polygon;
        this.remark = remark;
        this.addrState = addrState;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.townId = townId;
        this.villageId = villageId;
        this.streetId = streetId;
        this.groupId = groupId;
        this.communityId = communityId;
        this.buildingId = buildingId;
        this.unitId = unitId;
        this.floorId = floorId;
        this.roomId = roomId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShorthandCode() {
        return shorthandCode;
    }

    public void setShorthandCode(String shorthandCode) {
        this.shorthandCode = shorthandCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamePinyin() {
        return namePinyin;
    }

    public void setNamePinyin(String namePinyin) {
        this.namePinyin = namePinyin;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getLastThreeValue() {
        return lastThreeValue;
    }

    public void setLastThreeValue(String lastThreeValue) {
        this.lastThreeValue = lastThreeValue;
    }

    public Integer getLastThreeUnit() {
        return lastThreeUnit;
    }

    public void setLastThreeUnit(Integer lastThreeUnit) {
        this.lastThreeUnit = lastThreeUnit;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPolygon() {
        return polygon;
    }

    public void setPolygon(String polygon) {
        this.polygon = polygon;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getAddrState() {
        return addrState;
    }

    public void setAddrState(Integer addrState) {
        this.addrState = addrState;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getCountyId() {
        return countyId;
    }

    public void setCountyId(Long countyId) {
        this.countyId = countyId;
    }

    public Long getTownId() {
        return townId;
    }

    public void setTownId(Long townId) {
        this.townId = townId;
    }

    public Long getVillageId() {
        return villageId;
    }

    public void setVillageId(Long villageId) {
        this.villageId = villageId;
    }

    public Long getStreetId() {
        return streetId;
    }

    public void setStreetId(Long streetId) {
        this.streetId = streetId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getFloorId() {
        return floorId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}
