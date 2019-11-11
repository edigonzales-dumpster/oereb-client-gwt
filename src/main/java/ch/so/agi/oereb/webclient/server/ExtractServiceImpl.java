package ch.so.agi.oereb.webclient.server;

import javax.servlet.ServletException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import ch.so.agi.oereb.webclient.shared.ExtractServiceException;
import ch.so.agi.oereb.webclient.shared.ExtractResponse;
import ch.so.agi.oereb.webclient.shared.ExtractService;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ExtractServiceImpl extends RemoteServiceServlet implements ExtractService {
    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    PlrService oerebService;
    
    @Autowired
    CadastralSurveyingService cadastralSurveyingService;


    // see:
    // https://stackoverflow.com/questions/51874785/gwt-spring-boot-autowired-is-not-working
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }
    
    @Override
    public ExtractResponse extractServer(String egrid) throws ExtractServiceException {
        ExtractResponse response = new ExtractResponse();
        
        ch.so.agi.oereb.webclient.shared.models.plr.Extract plrExtract = oerebService.getExtract(egrid);
        response.setPlrExtract(plrExtract);
        
        ch.so.agi.oereb.webclient.shared.models.cadastralsurveying.Extract cadastralSurveyingExtract = cadastralSurveyingService.getExtract(egrid);
        response.setCadastralSurveyingExtract(cadastralSurveyingExtract);
        
        return response;
    }
}
