package com.demo.budjetkeeperver2.JSON.api;

import com.demo.budjetkeeperver2.JSON.pojo.Currency;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface APIService {
    @GET("live?access_key=88f75bbfb684272ce2be7da63450ac47&currencies=EUR,RUB&source=USD&format=1") // конец URL ля ретрофита (после слэша) указывается тут и называется "EndPoint"
    Observable<Currency> getCurrencies();
}
