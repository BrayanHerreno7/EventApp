package com.example.eventapp.Class;

import android.graphics.Bitmap;

public class Product {
    private String Id = "";
    private String IdFavorites = "";
    private String IdCar = "";
    private String IdPurchases = "";
    private String Name = "";
    private String Description = "";
    private int WholesaleCost = 0;
    private int WholesaleQuantity = 0;
    private int UnitCost = 0;
    private Boolean State = false;
    private String Department = "";
    private String City = "";
    private Bitmap ImageP ;
    private int Qualification = 0;

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }
    public void setDescription(String description) {
        Description = description;
    }

    public int getWholesaleCost() {
        return WholesaleCost;
    }
    public void setWholesaleCost(int wholesaleCost) {
        WholesaleCost = wholesaleCost;
    }

    public int getWholesaleQuantity() {
        return WholesaleQuantity;
    }
    public void setWholesaleQuantity(int wholesaleQuantity) { WholesaleQuantity = wholesaleQuantity; }

    public int getUnitCost() {
        return UnitCost;
    }
    public void setUnitCost(int unitCost) {
        UnitCost = unitCost;
    }

    public Boolean getState() {
        return State;
    }
    public void setState(Boolean state) {
        State = state;
    }
    public String getDepartament() {
        return Department;
    }
    public void setDepartament(String departament) {
        Department = departament;
    }
    public String getCity() {
        return City;
    }
    public void setCity(String city) {
        City = city;
    }
    public Bitmap getImage() {
        return ImageP;
    }
    public void setImage(Bitmap imagep) {
        ImageP = imagep;
    }
    public String getId() {
        return Id;
    }
    public void setId(String id) {
        Id = id;
    }
    public String getIdFavorites() {
        return IdFavorites;
    }
    public void setIdFavorites(String idFavorites) {
        IdFavorites = idFavorites;
    }

    public String getIdCar() {
        return IdCar;
    }
    public void setIdCar(String idCar) {
        IdCar = idCar;
    }
    public String getIdPurchases() {
        return IdPurchases;
    }
    public void setIdPurchases(String idPurchases) {
        IdPurchases= idPurchases;
    }
    public int getQualification() {
        return Qualification;
    }
    public void setIdQualification(int qualification) {
        Qualification = qualification;
    }
}
