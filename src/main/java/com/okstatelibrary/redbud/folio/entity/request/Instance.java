package com.okstatelibrary.redbud.folio.entity.request; 
import java.util.ArrayList; 
public class Instance{
    public String title;
    public ArrayList<Identifier> identifiers;
    public ArrayList<ContributorName> contributorNames;
    public ArrayList<Publication> publication;
    public ArrayList<String> editions;
}
