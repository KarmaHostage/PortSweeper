package com.karmahostage.portsweeper.scanning.service;

import com.karmahostage.portsweeper.scanning.model.ScanTarget;

import java.util.List;

public class ScanServiceImpl implements ScanService {

    @Override
    public void doScan(List<ScanTarget> target) {
        new ScanTask()
                .execute(target.toArray(new ScanTarget[target.size()]));
    }
}
