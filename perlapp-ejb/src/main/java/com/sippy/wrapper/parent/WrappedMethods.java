package com.sippy.wrapper.parent;

import com.sippy.wrapper.parent.database.DatabaseConnection;
import com.sippy.wrapper.parent.database.dao.Tnb;
import com.sippy.wrapper.parent.database.dao.TnbDao;
import com.sippy.wrapper.parent.request.Params;
import com.sippy.wrapper.parent.request.JavaTestRequest;
import com.sippy.wrapper.parent.response.JavaTestResponse;

import java.util.*;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class WrappedMethods {

  private static final Logger LOGGER = LoggerFactory.getLogger(WrappedMethods.class);

  @EJB DatabaseConnection databaseConnection;

  @RpcMethod(name = "javaTest", description = "Check if everything works :)")
  public Map<String, Object> javaTest(JavaTestRequest request) {
    JavaTestResponse response = new JavaTestResponse();

    int count = databaseConnection.getAllTnbs().size();

    LOGGER.info("the count is: " + count);

    response.setId(request.getId());
    String tempFeeling = request.isTemperatureOver20Degree() ? "warm" : "cold";
    response.setOutput(
        String.format(
            "%s has a rather %s day. And he has %d tnbs", request.getName(), tempFeeling, count));

    Map<String, Object> jsonResponse = new HashMap<>();
    jsonResponse.put("faultCode", "200");
    jsonResponse.put("faultString", "Method success");
    jsonResponse.put("something", response);

    return jsonResponse;
  }

  @RpcMethod(name="getTnbList")
  public Map<String, Object> getTnbList(Params params) {

    String number = params.getNumber();

    LOGGER.info("Fetching TNB list from the database");
    final List<TnbDao> tnbs_from_db = databaseConnection.getAllTnbs();


    List<TnbDao> tnb_with_number = new ArrayList<>();

    if (number != null){
      tnb_with_number = databaseConnection.getNumberTnbs(number);
    }

    List<TnbDao> tnbs = new ArrayList<TnbDao>();

    Tnb new_tnb = new Tnb();
    new_tnb.setTnb("D001");
    new_tnb.setName("Deutsche Telekom");

    if(tnb_with_number.contains(new_tnb) && tnb_with_number.getFirst().getTnb() == "D001"){
      new_tnb.setIsTnb(true);}
    else {
      new_tnb.setIsTnb(false);}

    tnbs.add(new_tnb);

    for (TnbDao element : tnbs_from_db){
      if(element.getTnb().matches("(D146|D218|D248)")){
        continue;
      }
      else{
        Tnb temp_tnb = new Tnb();
        temp_tnb.setName(element.getName());
        temp_tnb.setTnb(element.getTnb());

        if(tnb_with_number.contains(temp_tnb) && tnb_with_number.getFirst().getTnb() == element.getTnb()){
          temp_tnb.setIsTnb(true);}
        else {
          temp_tnb.setIsTnb(false);}

        tnbs.add(temp_tnb);
      }
    }

    List<TnbDao> sorted_tnbs = tnbs.stream().sorted(Comparator.comparing(name -> name.getName().toLowerCase())).toList();

    Map<String, Object> jsonResponse = new HashMap<>();
    jsonResponse.put("faultCode", "200");
    jsonResponse.put("faultString", "Method success");
    jsonResponse.put("tnbs", sorted_tnbs);

    return jsonResponse;
  }
}
