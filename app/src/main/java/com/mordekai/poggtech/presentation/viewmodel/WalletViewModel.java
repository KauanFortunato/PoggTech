package com.mordekai.poggtech.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Payments;
import com.mordekai.poggtech.data.model.Wallet;
import com.mordekai.poggtech.domain.WalletManager;

import java.util.List;

public class WalletViewModel extends ViewModel {
    private final WalletManager walletManager;

    private final MutableLiveData<Wallet> walletLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Payments>> paymentsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> depositSuccessLiveData = new MutableLiveData<>();

    // LiveData de erros separados
    private final MutableLiveData<String> walletError = new MutableLiveData<>();
    private final MutableLiveData<String> paymentsError = new MutableLiveData<>();
    private final MutableLiveData<String> depositError = new MutableLiveData<>();

    public WalletViewModel(WalletManager walletManager) {
        this.walletManager = walletManager;
    }

    // Getters públicos para o fragmento observar
    public LiveData<Wallet> getWalletLiveData() {
        return walletLiveData;
    }

    public LiveData<List<Payments>> getPaymentsLiveData() {
        return paymentsLiveData;
    }

    public LiveData<Boolean> getDepositSuccessLiveData() {
        return depositSuccessLiveData;
    }

    public LiveData<String> getWalletError() {
        return walletError;
    }

    public LiveData<String> getPaymentsError() {
        return paymentsError;
    }

    public LiveData<String> getDepositError() {
        return depositError;
    }

    // Métodos principais

    public void fetchWallet(int userId) {
        walletManager.getWallet(userId, new RepositoryCallback<Wallet>() {
            @Override
            public void onSuccess(Wallet data) {
                walletLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                walletError.postValue(t.getMessage());
            }
        });
    }

    public void fetchPayments(int userId) {
        walletManager.getPayments(userId, new RepositoryCallback<List<Payments>>() {
            @Override
            public void onSuccess(List<Payments> data) {
                paymentsLiveData.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                paymentsError.postValue(t.getMessage());
            }
        });
    }

    public void deposit(int userId, float amount) {
        walletManager.deposit(userId, amount, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                depositSuccessLiveData.postValue(true);
                fetchWallet(userId); // Atualiza carteira após depósito
            }

            @Override
            public void onFailure(Throwable t) {
                depositSuccessLiveData.postValue(false);
                depositError.postValue(t.getMessage());
            }
        });
    }
}
