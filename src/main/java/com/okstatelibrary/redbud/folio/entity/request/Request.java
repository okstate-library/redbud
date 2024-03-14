package com.okstatelibrary.redbud.folio.entity.request;

import java.util.Date;

import com.okstatelibrary.redbud.folio.entity.Metadata;
import com.okstatelibrary.redbud.folio.entity.loan.Loan;

public class Request{
    public String id;
    public String requestLevel;
    public String requestType;
    public Date requestDate;
    public String requesterId;
    public String instanceId;
    public String holdingsRecordId;
    public String itemId;
    public String status;
    public int position;
    public Instance instance;
    public Item item;
    public Requester requester;
    public String fulfilmentPreference;
    public String pickupServicePointId;
    public Metadata metadata;
    public Loan loan;
    public PickupServicePoint pickupServicePoint;
}
