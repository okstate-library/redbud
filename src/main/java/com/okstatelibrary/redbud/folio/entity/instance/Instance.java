package com.okstatelibrary.redbud.folio.entity.instance;

import com.okstatelibrary.redbud.folio.entity.Metadata;

import java.sql.Date;
import java.util.ArrayList;

public class Instance {

    public String id;
    public String _version;
    public String hrid;
    public String source;
    public String title;
    public ArrayList<String> administrativeNotes;
    public String indexTitle;
    public ArrayList<Object> parentInstances;
    public ArrayList<Object> childInstances;
    public boolean isBoundWith;
    public ArrayList<AlternativeTitle> alternativeTitles;
    public ArrayList<String> editions;
    public ArrayList<Object> series;
    public ArrayList<Identifier> identifiers;
    public ArrayList<Contributor> contributors;
    public ArrayList<Subject> subjects;
    public ArrayList<Classification> classifications;
    public ArrayList<Publication> publication;
    public ArrayList<Object> publicationFrequency;
    public ArrayList<Object> publicationRange;
    public ArrayList<Object> electronicAccess;
    public String instanceTypeId;
    public ArrayList<String> instanceFormatIds;
    public ArrayList<String> physicalDescriptions;
    public ArrayList<String> languages;
    public ArrayList<Note> notes;
    public String modeOfIssuanceId;
    public boolean previouslyHeld;
    public boolean staffSuppress;
    public boolean discoverySuppress;
    public ArrayList<String> statisticalCodeIds;
    public Date statusUpdatedDate;
    public Dates dates;
    public Metadata metadata;
    public Tags tags;
    public ArrayList<Object> natureOfContentTermIds;
    public PublicationPeriod publicationPeriod;
    public ArrayList<Object> precedingTitles;
    public ArrayList<SucceedingTitle> succeedingTitles;
}
