/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.vnpay.commoninterface.ott;

/**
 * @author nam
 */
public class NoticeResponse {

    private long t;
    private String e;
    private String m;

    /**
     * @return the e
     */
    public String getE() {
        return e;
    }

    /**
     * @param e the e to set
     */
    public void setE(String e) {
        this.e = e;
    }

    /**
     * @return the m
     */
    public String getM() {
        return m;
    }

    /**
     * @param m the m to set
     */
    public void setM(String m) {
        this.m = m;
    }

    public long getT() {
        return t;
    }

    public void setT(long t) {
        this.t = t;
    }

}
