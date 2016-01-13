package com.karmahostage.portsweeper.scanning.service;

import com.karmahostage.portsweeper.scanning.model.ScanTarget;

import java.util.List;

public interface ScanService {

    void doScan(List<ScanTarget> target);

}
