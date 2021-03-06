package com.karmahostage.portsweeper.infrastructure;

import com.karmahostage.portsweeper.ui.IpDetailActivity;
import com.karmahostage.portsweeper.ui.PingActivity;
import com.karmahostage.portsweeper.ui.PortSweeperActivity;

import dagger.Module;

@Module(
        injects = { PortSweeperActivity.class, IpDetailActivity.class, PingActivity.class },
        complete = false,
        library = true
)
public class PortSweeperModule {


}
