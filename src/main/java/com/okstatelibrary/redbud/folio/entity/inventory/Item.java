package com.okstatelibrary.redbud.folio.entity.inventory; 


import java.util.Date;

import com.okstatelibrary.redbud.folio.entity.Status; 
public class Item{
    public String id;
    public String itemId;
    public String userId;
    public String holdingsRecordId;
    public String instanceId;
    public String title;
    public String barcode;
    public String callNumber;
    public Status status;
    public MaterialType materialType;
    public Date loanDate;
    public Date dueDate;
}
