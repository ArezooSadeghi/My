package com.example.sipsupporterapp.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.database.SipSupporterDBHelper;
import com.example.sipsupporterapp.database.SipSupporterSchema;
import com.example.sipsupporterapp.model.AttachInfo;
import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CustomerPaymentInfo;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.CustomerProductInfo;
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.model.CustomerSupportInfo;
import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.model.CustomerUserResult;
import com.example.sipsupporterapp.model.DateResult;
import com.example.sipsupporterapp.model.PaymentInfo;
import com.example.sipsupporterapp.model.PaymentResult;
import com.example.sipsupporterapp.model.PaymentSubjectResult;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.SupportEventResult;
import com.example.sipsupporterapp.model.UserLoginParameter;
import com.example.sipsupporterapp.model.UserResult;
import com.example.sipsupporterapp.retrofit.AttachResultDeserializer;
import com.example.sipsupporterapp.retrofit.BankAccountResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerPaymentResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerProductResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerSupportResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerUserResultDeserializer;
import com.example.sipsupporterapp.retrofit.DateResultDeserializer;
import com.example.sipsupporterapp.retrofit.NoConnectivityException;
import com.example.sipsupporterapp.retrofit.PaymentResultDeserializer;
import com.example.sipsupporterapp.retrofit.PaymentSubjectResultDeserializer;
import com.example.sipsupporterapp.retrofit.ProductResultDeserializer;
import com.example.sipsupporterapp.retrofit.RetrofitInstance;
import com.example.sipsupporterapp.retrofit.SipSupporterService;
import com.example.sipsupporterapp.retrofit.SupportEventResultDeserializer;
import com.example.sipsupporterapp.retrofit.UserResultDeserializer;
import com.example.sipsupporterapp.utils.CheckStringIsNumeric;
import com.example.sipsupporterapp.viewmodel.SingleLiveEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SipSupporterRepository {

    public static SipSupporterRepository instance;
    private SQLiteDatabase database;
    private Context context;
    private SipSupporterService sipSupporterService;

    private static final String TAG = SipSupporterRepository.class.getSimpleName();

    private SingleLiveEvent<DateResult> dateResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerResult> customersResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomersResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<UserResult> userLoginResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorUserLoginResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<UserResult> changePasswordResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorChangePasswordResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerSupportResult> customerSupportsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomerSupportsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerSupportResult> addCustomerSupportResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAddCustomerSupportResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> customerProductsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomerProductsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> addCustomerProductResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAddCustomerProductResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> editCustomerProductResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorEditCustomerProductResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> deleteCustomerProductResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorDeleteCustomerProductResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> customerPaymentsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomerPaymentsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> addCustomerPaymentResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAddCustomerPaymentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> editCustomerPaymentResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorEditCustomerPaymentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> deleteCustomerPaymentResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorDeleteCustomerPaymentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> addPaymentResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAddPaymentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> editPaymentResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorEditPaymentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> deletePaymentResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorDeletePaymentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> customerProductAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomerProductAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> customerPaymentAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomerPaymentAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> customerSupportAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomerSupportAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> paymentAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorPaymentAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerUserResult> customerUsersResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomerUsersResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<SupportEventResult> supportEventsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorSupportEventsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> attachResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAttachResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> deleteAttachResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorDeleteAttachResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<ProductResult> productsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorProductsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<ProductResult> productInfoResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorProductInfoResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<ProductResult> addProductResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAddProductResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentSubjectResult> paymentSubjectsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorPaymentSubjectsSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentSubjectResult> paymentSubjectInfoResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorPaymentSubjectInfoResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> paymentsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorPaymentsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> attachResultViaAttachIDSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorAttachResultViaAttachIDSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> paymentsByBankAccountResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorPaymentsByBankAccountResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> customerPaymentsByBankAccountResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorCustomerPaymentsByBankAccountResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<BankAccountResult> bankAccountsResultSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> errorBankAccountsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> wrongIpAddressSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> dangerousUserSingleLiveEvent = new SingleLiveEvent<>();


    private SipSupporterRepository(Context context) {
        this.context = context.getApplicationContext();
        SipSupporterDBHelper helper = new SipSupporterDBHelper(context);
        database = helper.getWritableDatabase();
    }

    public static SipSupporterRepository getInstance(Context context) {
        if (instance == null) {
            instance = new SipSupporterRepository(context.getApplicationContext());
        }
        return instance;
    }

    public void getSipSupportServicePostUserLoginParameter(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<UserResult>() {
                }.getType(), new UserResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSupportServicePostCustomerParameter(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerResult>() {
                }.getType(), new CustomerResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceChangePassword(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<UserResult>() {
                }.getType(), new UserResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceCustomerSupportResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerSupportResult>() {
                }.getType(), new CustomerSupportResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceCustomerUserResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerUserResult>() {
                }.getType(), new CustomerUserResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceSupportEventResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<SupportEventResult>() {
                }.getType(), new SupportEventResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServicePostCustomerSupportResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerSupportResult>() {
                }.getType(), new CustomerSupportResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetDateResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<DateResult>() {
                }.getType(), new DateResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetCustomerProductResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerProductResult>() {
                }.getType(), new CustomerProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetProductResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<ProductResult>() {
                }.getType(), new ProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServicePostCustomerProducts(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerProductResult>() {
                }.getType(), new CustomerProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceForGetProductInfo(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<ProductResult>() {
                }.getType(), new ProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceForDeleteCustomerProduct(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerProductResult>() {
                }.getType(), new CustomerProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceForEditCustomerProduct(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerProductResult>() {
                }.getType(), new CustomerProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceAttach(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceCustomerPaymentResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new CustomerPaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetAttachmentFilesViaCustomerPaymentID(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetAttachmentFileViaAttachIDRetrofitInstance(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceAddCustomerPayments(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new CustomerPaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceEditCustomerPayments(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new CustomerPaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceDeleteCustomerPayments(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new CustomerPaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetBankAccountResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new BankAccountResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetAttachmentFilesViaCustomerProductID(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetAttachmentFilesViaCustomerSupportID(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServicePaymentsListByBankAccount(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentResult>() {
                }.getType(), new PaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServicePaymentsEdit(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentResult>() {
                }.getType(), new PaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServicePaymentsDelete(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentResult>() {
                }.getType(), new PaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServicePaymentsAdd(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentResult>() {
                }.getType(), new PaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServicePaymentSubjects(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentSubjectResult>() {
                }.getType(), new PaymentSubjectResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceDeleteAttach(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupportServiceGetAttachmentListByPaymentID(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServicePaymentInfo(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentSubjectResult>() {
                }.getType(), new PaymentSubjectResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServicePaymentsByBankAccount(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentResult>() {
                }.getType(), new PaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceCustomerPaymentsByBankAccount(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new CustomerPaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceBankAccounts(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new CustomerPaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public SingleLiveEvent<DateResult> getDateResultSingleLiveEvent() {
        return dateResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerResult> getCustomersResultSingleLiveEvent() {
        return customersResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomersResultSingleLiveEvent() {
        return errorCustomersResultSingleLiveEvent;
    }

    public SingleLiveEvent<UserResult> getUserLoginResultSingleLiveEvent() {
        return userLoginResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorUserLoginResultSingleLiveEvent() {
        return errorUserLoginResultSingleLiveEvent;
    }

    public SingleLiveEvent<UserResult> getChangePasswordResultSingleLiveEvent() {
        return changePasswordResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorChangePasswordResultSingleLiveEvent() {
        return errorChangePasswordResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerSupportResult> getCustomerSupportsResultSingleLiveEvent() {
        return customerSupportsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerSupportsResultSingleLiveEvent() {
        return errorCustomerSupportsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerSupportResult> getAddCustomerSupportResultSingleLiveEvent() {
        return addCustomerSupportResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddCustomerSupportResultSingleLiveEvent() {
        return errorAddCustomerSupportResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getCustomerProductsResultSingleLiveEvent() {
        return customerProductsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerProductsResultSingleLiveEvent() {
        return errorCustomerProductsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getAddCustomerProductResultSingleLiveEvent() {
        return addCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddCustomerProductResultSingleLiveEvent() {
        return errorAddCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getEditCustomerProductResultSingleLiveEvent() {
        return editCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorEditCustomerProductResultSingleLiveEvent() {
        return errorEditCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getDeleteCustomerProductResultSingleLiveEvent() {
        return deleteCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeleteCustomerProductResultSingleLiveEvent() {
        return errorDeleteCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getCustomerPaymentsResultSingleLiveEvent() {
        return customerPaymentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerPaymentsResultSingleLiveEvent() {
        return errorCustomerPaymentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getAddCustomerPaymentResultSingleLiveEvent() {
        return addCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddCustomerPaymentResultSingleLiveEvent() {
        return errorAddCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getEditCustomerPaymentResultSingleLiveEvent() {
        return editCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorEditCustomerPaymentResultSingleLiveEvent() {
        return errorEditCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getDeleteCustomerPaymentResultSingleLiveEvent() {
        return deleteCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeleteCustomerPaymentResultSingleLiveEvent() {
        return errorDeleteCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getAddPaymentResultSingleLiveEvent() {
        return addPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddPaymentResultSingleLiveEvent() {
        return errorAddPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getEditPaymentResultSingleLiveEvent() {
        return editPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorEditPaymentResultSingleLiveEvent() {
        return errorEditPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getDeletePaymentResultSingleLiveEvent() {
        return deletePaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeletePaymentResultSingleLiveEvent() {
        return errorDeletePaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<BankAccountResult> getBankAccountsResultSingleLiveEvent() {
        return bankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorBankAccountsResultSingleLiveEvent() {
        return errorBankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getCustomerProductAttachmentsResultSingleLiveEvent() {
        return customerProductAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerProductAttachmentsResultSingleLiveEvent() {
        return errorCustomerProductAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getCustomerPaymentAttachmentsResultSingleLiveEvent() {
        return customerPaymentAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerPaymentAttachmentsResultSingleLiveEvent() {
        return errorCustomerPaymentAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getCustomerSupportAttachmentsResultSingleLiveEvent() {
        return customerSupportAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerSupportAttachmentsResultSingleLiveEvent() {
        return errorCustomerSupportAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getPaymentAttachmentsResultSingleLiveEvent() {
        return paymentAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentAttachmentsResultSingleLiveEvent() {
        return errorPaymentAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerUserResult> getCustomerUsersResultSingleLiveEvent() {
        return customerUsersResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerUsersResultSingleLiveEvent() {
        return errorCustomerUsersResultSingleLiveEvent;
    }

    public SingleLiveEvent<SupportEventResult> getSupportEventsResultSingleLiveEvent() {
        return supportEventsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorSupportEventsResultSingleLiveEvent() {
        return errorSupportEventsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachResultSingleLiveEvent() {
        return attachResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAttachResultSingleLiveEvent() {
        return errorAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getDeleteAttachResultSingleLiveEvent() {
        return deleteAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorDeleteAttachResultSingleLiveEvent() {
        return errorDeleteAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getProductsResultSingleLiveEvent() {
        return productsResultSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getProductInfoResultSingleLiveEvent() {
        return productInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorProductInfoResultSingleLiveEvent() {
        return errorProductInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAddProductResultSingleLiveEvent() {
        return errorAddProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentSubjectResult> getPaymentSubjectsResultSingleLiveEvent() {
        return paymentSubjectsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentSubjectsSingleLiveEvent() {
        return errorPaymentSubjectsSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentSubjectResult> getPaymentSubjectInfoResultSingleLiveEvent() {
        return paymentSubjectInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getPaymentsResultSingleLiveEvent() {
        return paymentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentsResultSingleLiveEvent() {
        return errorPaymentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachResultViaAttachIDSingleLiveEvent() {
        return attachResultViaAttachIDSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorAttachResultViaAttachIDSingleLiveEvent() {
        return errorAttachResultViaAttachIDSingleLiveEvent;
    }

    public SingleLiveEvent<String> getNoConnectionExceptionHappenSingleLiveEvent() {
        return noConnectionExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getTimeoutExceptionHappenSingleLiveEvent() {
        return timeoutExceptionHappenSingleLiveEvent;
    }

    public SingleLiveEvent<String> getWrongIpAddressSingleLiveEvent() {
        return wrongIpAddressSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getDangerousUserSingleLiveEvent() {
        return dangerousUserSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getPaymentsByBankAccountResultSingleLiveEvent() {
        return paymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorPaymentsByBankAccountResultSingleLiveEvent() {
        return errorPaymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getCustomerPaymentsByBankAccountResultSingleLiveEvent() {
        return customerPaymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<String> getErrorCustomerPaymentsByBankAccountResultSingleLiveEvent() {
        return errorCustomerPaymentsByBankAccountResultSingleLiveEvent;
    }

    public void insertServerData(ServerData serverData) {
        ContentValues values = new ContentValues();

        values.put(SipSupporterSchema.ServerDataTable.Cols.CENTER_NAME, serverData.getCenterName());
        values.put(SipSupporterSchema.ServerDataTable.Cols.IP_ADDRESS, serverData.getIpAddress());
        values.put(SipSupporterSchema.ServerDataTable.Cols.PORT, serverData.getPort());

        database.insert(SipSupporterSchema.ServerDataTable.NAME, null, values);
    }

    public List<ServerData> getServerDataList() {
        List<ServerData> serverDataList = new ArrayList<>();
        Cursor cursor = database.query(
                SipSupporterSchema.ServerDataTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor == null || cursor.getCount() == 0) {
            return serverDataList;
        }

        try {

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                String centerName = cursor.getString(cursor.getColumnIndex(SipSupporterSchema.ServerDataTable.Cols.CENTER_NAME));
                String ipAddress = cursor.getString(cursor.getColumnIndex(SipSupporterSchema.ServerDataTable.Cols.IP_ADDRESS));
                String port = cursor.getString(cursor.getColumnIndex(SipSupporterSchema.ServerDataTable.Cols.PORT));

                ServerData serverData = new ServerData(centerName, ipAddress, port);

                serverDataList.add(serverData);

                cursor.moveToNext();
            }

        } finally {

            cursor.close();
        }
        return serverDataList;
    }

    public ServerData getServerData(String centerName) {
        ServerData serverData = new ServerData();
        String selection = "centerName=?";
        String[] selectionArgs = {centerName};
        Cursor cursor = database.query(
                SipSupporterSchema.ServerDataTable.NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (cursor == null || cursor.getCount() == 0) {
            return serverData;
        }

        try {

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                String ipAddress = cursor.getString(cursor.getColumnIndex(SipSupporterSchema.ServerDataTable.Cols.IP_ADDRESS));
                String port = cursor.getString(cursor.getColumnIndex(SipSupporterSchema.ServerDataTable.Cols.PORT));

                serverData.setCenterName(centerName);
                serverData.setIpAddress(ipAddress);
                serverData.setPort(port);

                cursor.moveToNext();
            }

        } finally {

            cursor.close();
        }
        return serverData;
    }

    public void deleteServerData(ServerData serverData) {
        String whereClause = "centerName=?";
        String whereArgs[] = {serverData.getCenterName()};
        database.delete(SipSupporterSchema.ServerDataTable.NAME, whereClause, whereArgs);
    }

    public void fetchUserResult(String path, UserLoginParameter userLoginParameter) {
        sipSupporterService.login(path, userLoginParameter)
                .enqueue(new Callback<UserResult>() {
                    @Override
                    public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                        if (response.isSuccessful()) {
                            userLoginResultSingleLiveEvent.setValue(response.body());
                        } else {
                            try {
                                Gson gson = new Gson();
                                UserResult userResult = gson.fromJson(response.errorBody().string(), UserResult.class);
                                if (CheckStringIsNumeric.isNumeric(userResult.getErrorCode())) {
                                    if (Integer.parseInt(userResult.getErrorCode()) <= -9001)
                                        dangerousUserSingleLiveEvent.setValue(true);
                                } else {
                                    errorUserLoginResultSingleLiveEvent.setValue(userResult.getError());
                                }
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResult> call, Throwable t) {
                        if (t instanceof NoConnectivityException) {
                            noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                        } else if (t instanceof SocketTimeoutException) {
                            timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                        } else {
                            wrongIpAddressSingleLiveEvent.setValue(context.getResources().getString(R.string.not_exist_server));
                        }
                    }
                });
    }

    public void fetchCustomerResult(String path, String userLoginKey, String customerName) {
        sipSupporterService.fetchCustomers(path, userLoginKey, customerName).enqueue(new Callback<CustomerResult>() {
            @Override
            public void onResponse(Call<CustomerResult> call, Response<CustomerResult> response) {
                if (response.isSuccessful()) {
                    customersResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerResult customerResult = gson.fromJson(response.errorBody().string(), CustomerResult.class);
                        try {
                            if (CheckStringIsNumeric.isNumeric(customerResult.getErrorCode())) {
                                if (Integer.parseInt(customerResult.getErrorCode()) <= -9001)
                                    dangerousUserSingleLiveEvent.setValue(true);
                            } else {
                                errorCustomersResultSingleLiveEvent.setValue(customerResult.getError());
                            }
                        } catch (NumberFormatException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void changePassword(String path, String userLoginKey, String newPassword) {
        sipSupporterService.changePassword(path, userLoginKey, newPassword).enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                if (response.isSuccessful()) {
                    changePasswordResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        UserResult userResult = gson.fromJson(response.errorBody().string(), UserResult.class);
                        if (CheckStringIsNumeric.isNumeric(userResult.getErrorCode())) {
                            if (Integer.parseInt(userResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorChangePasswordResultSingleLiveEvent.setValue(userResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void getCustomerSupportResult(String path, String userLoginKey, int customerID) {
        sipSupporterService.fetchCustomerSupports(path, userLoginKey, customerID).enqueue(new Callback<CustomerSupportResult>() {
            @Override
            public void onResponse(Call<CustomerSupportResult> call, Response<CustomerSupportResult> response) {
                if (response.isSuccessful()) {
                    customerSupportsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerSupportResult customerSupportResult = gson.fromJson(response.errorBody().string(), CustomerSupportResult.class);
                        if (CheckStringIsNumeric.isNumeric(customerSupportResult.getErrorCode())) {
                            if (Integer.parseInt(customerSupportResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorCustomerSupportsResultSingleLiveEvent.setValue(customerSupportResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerSupportResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }


    public void fetchCustomerUserResult(String path, String userLoginKey, int customerID) {
        sipSupporterService.fetchCustomerUsers(path, userLoginKey, customerID).enqueue(new Callback<CustomerUserResult>() {
            @Override
            public void onResponse(Call<CustomerUserResult> call, Response<CustomerUserResult> response) {
                if (response.isSuccessful()) {
                    customerUsersResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerUserResult customerUserResult = gson.fromJson(response.errorBody().string(), CustomerUserResult.class);
                        if (CheckStringIsNumeric.isNumeric(customerUserResult.getErrorCode())) {
                            if (Integer.parseInt(customerUserResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorCustomerUsersResultSingleLiveEvent.setValue(customerUserResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerUserResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }


    public void fetchSupportEventResult(String path, String userLoginKey) {
        sipSupporterService.fetchSupportEvents(path, userLoginKey).enqueue(new Callback<SupportEventResult>() {
            @Override
            public void onResponse(Call<SupportEventResult> call, Response<SupportEventResult> response) {
                if (response.isSuccessful()) {
                    supportEventsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        SupportEventResult supportEventResult = gson.fromJson(response.errorBody().string(), SupportEventResult.class);
                        if (CheckStringIsNumeric.isNumeric(supportEventResult.getErrorCode())) {
                            if (Integer.parseInt(supportEventResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorSupportEventsResultSingleLiveEvent.setValue(supportEventResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<SupportEventResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void postCustomerSupportInfo(String path, String userLoginKey, CustomerSupportInfo customerSupportInfo) {
        sipSupporterService.addCustomerSupport(path, userLoginKey, customerSupportInfo).enqueue(new Callback<CustomerSupportResult>() {
            @Override
            public void onResponse(Call<CustomerSupportResult> call, Response<CustomerSupportResult> response) {
                if (response.isSuccessful()) {
                    addCustomerSupportResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerSupportResult customerSupportResult = gson.fromJson(response.errorBody().string(), CustomerSupportResult.class);
                        if (CheckStringIsNumeric.isNumeric(customerSupportResult.getErrorCode())) {
                            if (Integer.parseInt(customerSupportResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorAddCustomerSupportResultSingleLiveEvent.setValue(customerSupportResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerSupportResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchDateResult(String path, String userLoginKey) {
        sipSupporterService.fetchDate(path, userLoginKey).enqueue(new Callback<DateResult>() {
            @Override
            public void onResponse(Call<DateResult> call, Response<DateResult> response) {
                if (response.isSuccessful()) {
                    dateResultSingleLiveEvent.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<DateResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchProductResult(String path, String userLoginKey, int customerID) {
        sipSupporterService.fetchCustomerProducts(path, userLoginKey, customerID).enqueue(new Callback<CustomerProductResult>() {
            @Override
            public void onResponse(Call<CustomerProductResult> call, Response<CustomerProductResult> response) {
                if (response.isSuccessful()) {
                    customerProductsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerProductResult customerProductResult = gson.fromJson(response.errorBody().string(), CustomerProductResult.class);
                        if (CheckStringIsNumeric.isNumeric(customerProductResult.getErrorCode())) {
                            if (Integer.parseInt(customerProductResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorCustomerProductsResultSingleLiveEvent.setValue(customerProductResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerProductResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchProductResult(String path, String userLoginKey) {
        sipSupporterService.fetchProducts(path, userLoginKey).enqueue(new Callback<ProductResult>() {
            @Override
            public void onResponse(Call<ProductResult> call, Response<ProductResult> response) {
                if (response.isSuccessful()) {
                    productsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        ProductResult productResult = gson.fromJson(response.errorBody().string(), ProductResult.class);
                        if (CheckStringIsNumeric.isNumeric(productResult.getErrorCode())) {
                            if (Integer.parseInt(productResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorProductsResultSingleLiveEvent.setValue(productResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void postCustomerProducts(String path, String userLoginKey, CustomerProductInfo customerProductInfo) {
        sipSupporterService.addCustomerProduct(path, userLoginKey, customerProductInfo).enqueue(new Callback<CustomerProductResult>() {
            @Override
            public void onResponse(Call<CustomerProductResult> call, Response<CustomerProductResult> response) {
                if (response.isSuccessful()) {
                    addCustomerProductResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerProductResult customerProductResult = gson.fromJson(response.errorBody().string(), CustomerProductResult.class);
                        if (CheckStringIsNumeric.isNumeric(customerProductResult.getErrorCode())) {
                            if (Integer.parseInt(customerProductResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorAddCustomerProductResultSingleLiveEvent.setValue(customerProductResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerProductResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchProductInfo(String path, String userLoginKey, int productID) {
        sipSupporterService.fetchProductInfo(path, userLoginKey, productID).enqueue(new Callback<ProductResult>() {
            @Override
            public void onResponse(Call<ProductResult> call, Response<ProductResult> response) {
                if (response.isSuccessful()) {
                    productInfoResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        ProductResult productResult = gson.fromJson(response.errorBody().string(), ProductResult.class);
                        if (CheckStringIsNumeric.isNumeric(productResult.getErrorCode())) {
                            if (Integer.parseInt(productResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorProductInfoResultSingleLiveEvent.setValue(productResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void deleteCustomerProduct(String path, String userLoginKey, int customerProductID) {
        sipSupporterService.deleteCustomerProduct(path, userLoginKey, customerProductID).enqueue(new Callback<CustomerProductResult>() {
            @Override
            public void onResponse(Call<CustomerProductResult> call, Response<CustomerProductResult> response) {
                if (response.isSuccessful()) {
                    deleteCustomerProductResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerProductResult customerProductResult = gson.fromJson(response.errorBody().string(), CustomerProductResult.class);
                        if (CheckStringIsNumeric.isNumeric(customerProductResult.getErrorCode())) {
                            if (Integer.parseInt(customerProductResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorDeleteCustomerProductResultSingleLiveEvent.setValue(customerProductResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerProductResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void editCustomerProduct(String path, String userLoginKey, CustomerProductInfo customerProductInfo) {
        sipSupporterService.editCustomerProduct(path, userLoginKey, customerProductInfo).enqueue(new Callback<CustomerProductResult>() {
            @Override
            public void onResponse(Call<CustomerProductResult> call, Response<CustomerProductResult> response) {
                if (response.isSuccessful()) {
                    editCustomerProductResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerProductResult customerProductResult = gson.fromJson(response.errorBody().string(), CustomerProductResult.class);
                        if (CheckStringIsNumeric.isNumeric(customerProductResult.getErrorCode())) {
                            if (Integer.parseInt(customerProductResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorEditCustomerProductResultSingleLiveEvent.setValue(customerProductResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerProductResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void attach(String path, String userLoginKey, AttachInfo attachInfo) {
        sipSupporterService.attach(path, userLoginKey, attachInfo).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    attachResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (CheckStringIsNumeric.isNumeric(attachResult.getErrorCode())) {
                            if (Integer.parseInt(attachResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorAttachResultSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchCustomerPaymentResult(String path, String userLoginKey, int customerID) {
        sipSupporterService.fetchCustomerPayments(path, userLoginKey, customerID).enqueue(new Callback<CustomerPaymentResult>() {
            @Override
            public void onResponse(Call<CustomerPaymentResult> call, Response<CustomerPaymentResult> response) {
                if (response.isSuccessful()) {
                    customerPaymentsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerPaymentResult customerPaymentResult = gson.fromJson(response.errorBody().string(), CustomerPaymentResult.class);
                        if (CheckStringIsNumeric.isNumeric(customerPaymentResult.getErrorCode())) {
                            if (Integer.parseInt(customerPaymentResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorCustomerPaymentsResultSingleLiveEvent.setValue(customerPaymentResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerPaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void getAttachmentFilesViaCustomerPaymentID(String path, String userLoginKey, int customerPaymentID, boolean LoadFileData) {
        sipSupporterService.fetchCustomerPaymentAttachments(path, userLoginKey, customerPaymentID, LoadFileData).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    customerPaymentAttachmentsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (CheckStringIsNumeric.isNumeric(attachResult.getErrorCode())) {
                            if (Integer.parseInt(attachResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorCustomerPaymentAttachmentsResultSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void addCustomerPayments(String path, String userLoginKey, CustomerPaymentInfo customerPaymentInfo) {
        sipSupporterService.addCustomerPayment(path, userLoginKey, customerPaymentInfo).enqueue(new Callback<CustomerPaymentResult>() {
            @Override
            public void onResponse(Call<CustomerPaymentResult> call, Response<CustomerPaymentResult> response) {
                if (response.isSuccessful()) {
                    addCustomerPaymentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (CheckStringIsNumeric.isNumeric(attachResult.getErrorCode())) {
                            if (Integer.parseInt(attachResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorAddCustomerPaymentResultSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerPaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void editCustomerPayments(String path, String userLoginKey, CustomerPaymentInfo customerPaymentInfo) {
        sipSupporterService.editCustomerPayment(path, userLoginKey, customerPaymentInfo).enqueue(new Callback<CustomerPaymentResult>() {
            @Override
            public void onResponse(Call<CustomerPaymentResult> call, Response<CustomerPaymentResult> response) {
                if (response.isSuccessful()) {
                    editCustomerPaymentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (CheckStringIsNumeric.isNumeric(attachResult.getErrorCode())) {
                            if (Integer.parseInt(attachResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorEditCustomerPaymentResultSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerPaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void deleteCustomerPayments(String path, String userLoginKey, int customerPaymentID) {
        sipSupporterService.deleteCustomerPayment(path, userLoginKey, customerPaymentID).enqueue(new Callback<CustomerPaymentResult>() {
            @Override
            public void onResponse(Call<CustomerPaymentResult> call, Response<CustomerPaymentResult> response) {
                if (response.isSuccessful()) {
                    deleteCustomerPaymentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (CheckStringIsNumeric.isNumeric(attachResult.getErrorCode())) {
                            if (Integer.parseInt(attachResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorDeleteCustomerPaymentResultSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerPaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchFileWithCustomerProductID(String path, String userLoginKey, int customerProductID, boolean LoadFileData) {
        sipSupporterService.fetchCustomerProductAttachments(path, userLoginKey, customerProductID, LoadFileData).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    customerProductAttachmentsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (CheckStringIsNumeric.isNumeric(attachResult.getErrorCode())) {
                            if (Integer.parseInt(attachResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorCustomerProductAttachmentsResultSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchFileWithCustomerSupportID(String path, String userLoginKey, int customerSupportID, boolean LoadFileData) {
        sipSupporterService.fetchCustomerSupportAttachments(path, userLoginKey, customerSupportID, LoadFileData).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    customerSupportAttachmentsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (CheckStringIsNumeric.isNumeric(attachResult.getErrorCode())) {
                            if (Integer.parseInt(attachResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorCustomerSupportAttachmentsResultSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchWithAttachID(String path, String userLoginKey, int attachID, boolean loadFileData) {
        sipSupporterService.fetchAttachInfo(path, userLoginKey, attachID, loadFileData).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    attachResultViaAttachIDSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (CheckStringIsNumeric.isNumeric(attachResult.getErrorCode())) {
                            if (Integer.parseInt(attachResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorAttachResultViaAttachIDSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchPaymentsListByBankAccounts(String path, String userLoginKey, int bankAccountID) {
        sipSupporterService.fetchPayments(path, userLoginKey, bankAccountID).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                if (response.isSuccessful()) {
                    paymentsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentResult paymentResult = gson.fromJson(response.errorBody().string(), PaymentResult.class);
                        if (CheckStringIsNumeric.isNumeric(paymentResult.getErrorCode())) {
                            if (Integer.parseInt(paymentResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorPaymentsResultSingleLiveEvent.setValue(paymentResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void paymentsEdit(String path, String userLoginKey, PaymentInfo paymentInfo) {
        sipSupporterService.editPayment(path, userLoginKey, paymentInfo).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                if (response.isSuccessful()) {
                    editPaymentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentResult paymentResult = gson.fromJson(response.errorBody().string(), PaymentResult.class);
                        if (CheckStringIsNumeric.isNumeric(paymentResult.getErrorCode())) {
                            if (Integer.parseInt(paymentResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorEditPaymentResultSingleLiveEvent.setValue(paymentResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void paymentsDelete(String path, String userLoginKey, int paymentID) {
        sipSupporterService.deletePayment(path, userLoginKey, paymentID).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                if (response.isSuccessful()) {
                    deletePaymentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentResult paymentResult = gson.fromJson(response.errorBody().string(), PaymentResult.class);
                        if (CheckStringIsNumeric.isNumeric(paymentResult.getErrorCode())) {
                            if (Integer.parseInt(paymentResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorDeletePaymentResultSingleLiveEvent.setValue(paymentResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchPaymentSubjects(String path, String userLoginKey) {
        sipSupporterService.fetchPaymentSubjects(path, userLoginKey).enqueue(new Callback<PaymentSubjectResult>() {
            @Override
            public void onResponse(Call<PaymentSubjectResult> call, Response<PaymentSubjectResult> response) {
                if (response.isSuccessful()) {
                    paymentSubjectsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentSubjectResult paymentSubjectResult = gson.fromJson(response.errorBody().string(), PaymentSubjectResult.class);
                        if (CheckStringIsNumeric.isNumeric(paymentSubjectResult.getErrorCode())) {
                            if (Integer.parseInt(paymentSubjectResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorPaymentSubjectsSingleLiveEvent.setValue(paymentSubjectResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentSubjectResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void paymentsAdd(String path, String userLoginKey, PaymentInfo paymentInfo) {
        sipSupporterService.addPayment(path, userLoginKey, paymentInfo).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                if (response.isSuccessful()) {
                    addPaymentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentSubjectResult paymentSubjectResult = gson.fromJson(response.errorBody().string(), PaymentSubjectResult.class);
                        if (CheckStringIsNumeric.isNumeric(paymentSubjectResult.getErrorCode())) {
                            if (Integer.parseInt(paymentSubjectResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorAddPaymentResultSingleLiveEvent.setValue(paymentSubjectResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void deleteAttach(String path, String userLoginKey, int attachID) {
        sipSupporterService.deleteAttach(path, userLoginKey, attachID).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    deleteAttachResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (CheckStringIsNumeric.isNumeric(attachResult.getErrorCode())) {
                            if (Integer.parseInt(attachResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorDeleteAttachResultSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchAttachmentsByPaymentID(String path, String userLoginKey, int paymentID, boolean LoadFileData) {
        sipSupporterService.fetchPaymentAttachments(path, userLoginKey, paymentID, LoadFileData).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    paymentAttachmentsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (CheckStringIsNumeric.isNumeric(attachResult.getErrorCode())) {
                            if (Integer.parseInt(attachResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorPaymentAttachmentsResultSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AttachResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchPaymentSubjectInfo(String path, String userLoginKey, int paymentSubjectID) {
        sipSupporterService.fetchPaymentInfo(path, userLoginKey, paymentSubjectID).enqueue(new Callback<PaymentSubjectResult>() {
            @Override
            public void onResponse(Call<PaymentSubjectResult> call, Response<PaymentSubjectResult> response) {
                if (response.isSuccessful()) {
                    paymentSubjectInfoResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        if (CheckStringIsNumeric.isNumeric(attachResult.getErrorCode())) {
                            if (Integer.parseInt(attachResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorPaymentSubjectInfoResultSingleLiveEvent.setValue(attachResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentSubjectResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchPaymentsByBankAccount(String path, String userLoginKey, int bankAccountID) {
        sipSupporterService.fetchPaymentsByBankAccount(path, userLoginKey, bankAccountID).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                if (response.isSuccessful()) {
                    paymentsByBankAccountResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentResult paymentResult = gson.fromJson(response.errorBody().string(), PaymentResult.class);
                        if (CheckStringIsNumeric.isNumeric(paymentResult.getErrorCode())) {
                            if (Integer.parseInt(paymentResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorPaymentsByBankAccountResultSingleLiveEvent.setValue(paymentResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchCustomerPaymentsByBankAccount(String path, String userLoginKey, int bankAccountID) {
        sipSupporterService.fetchCustomerPaymentsByBankAccount(path, userLoginKey, bankAccountID).enqueue(new Callback<CustomerPaymentResult>() {
            @Override
            public void onResponse(Call<CustomerPaymentResult> call, Response<CustomerPaymentResult> response) {
                if (response.isSuccessful()) {
                    customerPaymentsByBankAccountResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerPaymentResult customerPaymentResult = gson.fromJson(response.errorBody().string(), CustomerPaymentResult.class);
                        if (CheckStringIsNumeric.isNumeric(customerPaymentResult.getErrorCode())) {
                            if (Integer.parseInt(customerPaymentResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorCustomerPaymentsByBankAccountResultSingleLiveEvent.setValue(customerPaymentResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomerPaymentResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }

    public void fetchBankAccounts(String path, String userLoginKey) {
        sipSupporterService.fetchBankAccounts(path, userLoginKey).enqueue(new Callback<BankAccountResult>() {
            @Override
            public void onResponse(Call<BankAccountResult> call, Response<BankAccountResult> response) {
                if (response.isSuccessful()) {
                    bankAccountsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        BankAccountResult bankAccountResult = gson.fromJson(response.errorBody().string(), BankAccountResult.class);
                        if (CheckStringIsNumeric.isNumeric(bankAccountResult.getErrorCode())) {
                            if (Integer.parseInt(bankAccountResult.getErrorCode()) <= -9001)
                                dangerousUserSingleLiveEvent.setValue(true);
                        } else {
                            errorBankAccountsResultSingleLiveEvent.setValue(bankAccountResult.getError());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<BankAccountResult> call, Throwable t) {
                if (t instanceof NoConnectivityException) {
                    noConnectionExceptionHappenSingleLiveEvent.setValue(t.getMessage());
                } else if (t instanceof SocketTimeoutException) {
                    timeoutExceptionHappenSingleLiveEvent.setValue(context.getResources().getString(R.string.timeout_exception_happen_message));
                } else {
                    Log.e(TAG, t.getMessage(), t);
                }
            }
        });
    }
}
