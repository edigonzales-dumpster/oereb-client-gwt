package ch.so.agi.oereb.webclient.server.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonPropertyOrder({ "display", "id_field_name", "id_field_type", "dataproduct_id", "feature_id" })
public class Feature {

    @JsonProperty("display")
    private String display;
    @JsonProperty("id_field_name")
    private String idFieldName;
    @JsonProperty("id_field_type")
    private Boolean idFieldType;
    @JsonProperty("dataproduct_id")
    private String dataproductId;
    @JsonProperty("feature_id")
    private Integer featureId;

    @JsonProperty("display")
    public String getDisplay() {
        return display;
    }

    @JsonProperty("display")
    public void setDisplay(String display) {
        this.display = display;
    }

    @JsonProperty("id_field_name")
    public String getIdFieldName() {
        return idFieldName;
    }

    @JsonProperty("id_field_name")
    public void setIdFieldName(String idFieldName) {
        this.idFieldName = idFieldName;
    }

    @JsonProperty("id_field_type")
    public Boolean getIdFieldType() {
        return idFieldType;
    }

    @JsonProperty("id_field_type")
    public void setIdFieldType(Boolean idFieldType) {
        this.idFieldType = idFieldType;
    }

    @JsonProperty("dataproduct_id")
    public String getDataproductId() {
        return dataproductId;
    }

    @JsonProperty("dataproduct_id")
    public void setDataproductId(String dataproductId) {
        this.dataproductId = dataproductId;
    }

    @JsonProperty("feature_id")
    public Integer getFeatureId() {
        return featureId;
    }

    @JsonProperty("feature_id")
    public void setFeatureId(Integer featureId) {
        this.featureId = featureId;
    }

}
