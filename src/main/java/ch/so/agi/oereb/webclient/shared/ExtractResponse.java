package ch.so.agi.oereb.webclient.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import ch.so.agi.oereb.webclient.shared.models.plr.Extract;

public class ExtractResponse implements IsSerializable {
    private ch.so.agi.oereb.webclient.shared.models.plr.Extract plrExtract;

    private ch.so.agi.oereb.webclient.shared.models.cadastralsurveying.Extract cadastralSurveyingExtract;

    public ch.so.agi.oereb.webclient.shared.models.plr.Extract getPlrExtract() {
        return plrExtract;
    }

    public void setPlrExtract(ch.so.agi.oereb.webclient.shared.models.plr.Extract plrExtract) {
        this.plrExtract = plrExtract;
    }

    public ch.so.agi.oereb.webclient.shared.models.cadastralsurveying.Extract getCadastralSurveyingExtract() {
        return cadastralSurveyingExtract;
    }

    public void setCadastralSurveyingExtract(
            ch.so.agi.oereb.webclient.shared.models.cadastralsurveying.Extract cadastralSurveyingExtract) {
        this.cadastralSurveyingExtract = cadastralSurveyingExtract;
    }
}
