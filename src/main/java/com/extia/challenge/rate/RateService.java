package com.extia.challenge.rate;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.extia.challenge.model.ExchangeResultDTO;
import com.extia.challenge.xml.DataSet;
import com.extia.challenge.xml.LTCube;
import com.extia.challenge.xml.LTHeader;
import com.extia.challenge.xml.LTCube.Rate;

import org.springframework.stereotype.Service;

@Service
public class RateService {
    
    private static final String path = "http://www.bnro.ro/nbrfxrates.xml";

    public ExchangeResultDTO getExchangeResult(String currency1, String currency2) {
        try {
            URL url = new URL(path);
            DataSet xmlDataSet = unmarshalXML(url);
			return extractData(xmlDataSet, currency1, currency2);
        } catch (Exception e) {
            return null;
        }
        
	}
	
	private static DataSet unmarshalXML(URL url) throws Exception {
        final JAXBContext jc = JAXBContext.newInstance(DataSet.class);
        final Unmarshaller u = jc.createUnmarshaller();
        try {
            return (DataSet) u.unmarshal(url);
        } catch (JAXBException e) {
            File f = new File("src\\main\\java\\com\\extia\\challenge\\xml\\nbrfxrates.xml");
            return (DataSet) u.unmarshal(f);
        }
    }

    private static ExchangeResultDTO extractData(DataSet data, String currency1, String currency2) {
        ExchangeResultDTO result = new ExchangeResultDTO();
        LTHeader header = data.getHeader();
        DataSet.Body body = data.getBody();
        result.setDate(header.getPublishingDate().toString());
        result.setDetails(header.getPublisher());
        if (currency2.equals("RON")) {
            computeParityForRON(body, result, currency1);
        } else {
            computeParityForWild(body, result, currency1, currency2);
        }
        return result;
    }
    
    private static void computeParityForRON(DataSet.Body body, ExchangeResultDTO result, String currency1) {
        for (LTCube cube : body.getCube()) {
            for (Rate rate : cube.getRate()) {
                if (rate.getCurrency().equalsIgnoreCase(currency1)) {
                    if (rate.getMultiplier() == null) {
                        result.setParity(String.valueOf(rate.getValue()));    
                    } else {
                        result.setParity(String.valueOf(rate.getValue().divide(new BigDecimal(rate.getMultiplier()), 3, RoundingMode.HALF_UP)));
                    }
                    break;
                }    
            }
            break;
        }
    }

    private static void computeParityForWild(DataSet.Body body, ExchangeResultDTO result, String currency1, String currency2) {
        BigDecimal value1 = null;
        BigDecimal value2 = null;
        for (LTCube cube : body.getCube()) {
            for (Rate rate : cube.getRate()) {
                if (rate.getCurrency().equalsIgnoreCase(currency1)) {
                    if (rate.getMultiplier() == null) {
                        value1 = rate.getValue();
                    } else {
                        value1 = rate.getValue().divide(new BigDecimal(rate.getMultiplier()), 3, RoundingMode.HALF_UP);
                    }
                }
                if (rate.getCurrency().equalsIgnoreCase(currency2)) {
                    if (rate.getMultiplier() == null) {
                        value2 = rate.getValue();
                    } else {
                        value2 = rate.getValue().divide(new BigDecimal(rate.getMultiplier()), 3, RoundingMode.HALF_UP);
                    }
                }
            }
        }
        if (value1 != null && value2 != null) {
            result.setParity(String.valueOf(value1.divide(value2, 3, RoundingMode.HALF_UP)));
        }
    }

    private static void cacheFile() {
        try (InputStream in = URI.create(path).toURL().openStream()) {
            try {
                Files.copy(in, Paths.get("src\\main\\java\\com\\extia\\challenge\\xml\\nbrfxrates.xml"));
            } catch (Exception e) {
                //log
            }
        } catch (Exception e) {
            //log
        }
    }

}
