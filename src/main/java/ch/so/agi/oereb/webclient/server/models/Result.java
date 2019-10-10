package ch.so.agi.oereb.webclient.server.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonPropertyOrder({ "feature" })
public class Result {

    @JsonProperty("feature")
    private Feature feature;

    @JsonProperty("feature")
    public Feature getFeature() {
        return feature;
    }

    @JsonProperty("feature")
    public void setFeature(Feature feature) {
        this.feature = feature;
    }

}
