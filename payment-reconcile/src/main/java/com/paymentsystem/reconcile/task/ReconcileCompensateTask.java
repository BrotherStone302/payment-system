package com.paymentsystem.reconcile.task;

import com.paymentsystem.reconcile.service.ReconcileService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReconcileCompensateTask {

    private final ReconcileService reconcileService;

    public ReconcileCompensateTask(ReconcileService reconcileService) {
        this.reconcileService = reconcileService;
    }

    @Scheduled(fixedDelay = 30000)
    public void autoCompensate() {
        String result = reconcileService.compensate();
        System.out.println("========== 定时补偿任务执行结果: " + result + " ==========");
    }
}
