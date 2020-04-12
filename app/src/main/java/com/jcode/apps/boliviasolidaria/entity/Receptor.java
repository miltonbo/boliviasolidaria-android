package com.jcode.apps.boliviasolidaria.entity;

import java.util.List;

public class Receptor {

    private long id;
    private String empresa;

    private List<BankAccount> cuentas;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public List<BankAccount> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<BankAccount> cuentas) {
        this.cuentas = cuentas;
    }
}
