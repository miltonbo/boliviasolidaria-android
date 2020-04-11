package com.jcode.apps.boliviasolidaria.entity;

import java.io.Serializable;

public class HelpRequest implements Serializable {

    private long id;
    private String nombre;
    private String direccion;
    private String necesidad;
    private String contacto;
    private String ci;
    private double lat;
    private double lng;
    private long fechahoraSolicitud;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNecesidad() {
        return necesidad;
    }

    public void setNecesidad(String necesidad) {
        this.necesidad = necesidad;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getFechahoraSolicitud() {
        return fechahoraSolicitud;
    }

    public void setFechahoraSolicitud(long fechahoraSolicitud) {
        this.fechahoraSolicitud = fechahoraSolicitud;
    }
}
