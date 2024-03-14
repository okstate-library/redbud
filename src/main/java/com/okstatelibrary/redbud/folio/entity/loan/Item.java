package com.okstatelibrary.redbud.folio.entity.loan; 
import java.util.ArrayList;

import com.okstatelibrary.redbud.folio.entity.Status; 
public class Item{
    public String id;
    public String holdingsRecordId;
    public String instanceId;
    public String title;
    public String barcode;
    public ArrayList<Contributor> contributors;
    public CallNumberComponents callNumberComponents;
    public Status status;
    public Location location;
    public MaterialType materialType;
    public String callNumber;
    public String copyNumber;
}
