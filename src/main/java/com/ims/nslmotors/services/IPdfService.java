package com.ims.nslmotors.services;

public interface IPdfService {

    /**
     * HTML içeriğinden PDF oluşturur
     * 
     * @param htmlContent HTML içerik
     * @return PDF dosyası byte array olarak
     */
    byte[] generatePdfFromHtml(String htmlContent);
}
