package com.ym.asych;


//只是把两个方法对外暴露
public class DelayAdditionFuture extends AbstractFuture<Integer> {

    @Override
    public IFuture<Integer> setSuccess(Object result) {
        return super.setSuccess(result);
    }

    @Override
    public IFuture<Integer> setFailure(Throwable cause) {
        return super.setFailure(cause);
    }

}