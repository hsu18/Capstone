package com.example.capstone.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllCert {
    public AllCert(Cert cert, List<Date> dates) {
        this.cert = cert;
        this.dates = dates;
    }

    Cert cert;
    List<Date> dates;
}
