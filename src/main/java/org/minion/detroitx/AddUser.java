package org.minion.detroitx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties
public class AddUser {    
    
    protected String repository;
    
    protected String facade;
    
  
}
