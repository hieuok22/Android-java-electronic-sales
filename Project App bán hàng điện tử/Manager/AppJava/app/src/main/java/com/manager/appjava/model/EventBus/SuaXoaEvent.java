package com.manager.appjava.model.EventBus;

import com.manager.appjava.model.SamPhamMoi;

public class SuaXoaEvent {
   SamPhamMoi samPhamMoi;

    public SuaXoaEvent(SamPhamMoi samPhamMoi) {
        this.samPhamMoi = samPhamMoi;
    }

    public SamPhamMoi getSamPhamMoi() {
        return samPhamMoi;
    }

    public void setSamPhamMoi(SamPhamMoi samPhamMoi) {
        this.samPhamMoi = samPhamMoi;
    }
}
