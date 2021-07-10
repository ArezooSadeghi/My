package com.example.sipsupporterapp.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.sipsupporterapp.R;
import com.example.sipsupporterapp.database.SipSupporterDBHelper;
import com.example.sipsupporterapp.database.SipSupporterSchema;
import com.example.sipsupporterapp.model.AssignResult;
import com.example.sipsupporterapp.model.AttachResult;
import com.example.sipsupporterapp.model.BankAccountResult;
import com.example.sipsupporterapp.model.CaseProductResult;
import com.example.sipsupporterapp.model.CaseResult;
import com.example.sipsupporterapp.model.CaseTypeResult;
import com.example.sipsupporterapp.model.CommentResult;
import com.example.sipsupporterapp.model.CustomerPaymentResult;
import com.example.sipsupporterapp.model.CustomerProductResult;
import com.example.sipsupporterapp.model.CustomerResult;
import com.example.sipsupporterapp.model.CustomerSupportResult;
import com.example.sipsupporterapp.model.CustomerUserResult;
import com.example.sipsupporterapp.model.DateResult;
import com.example.sipsupporterapp.model.PaymentResult;
import com.example.sipsupporterapp.model.PaymentSubjectResult;
import com.example.sipsupporterapp.model.ProductGroupResult;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.ServerData;
import com.example.sipsupporterapp.model.SupportEventResult;
import com.example.sipsupporterapp.model.UserResult;
import com.example.sipsupporterapp.retrofit.AssignResultDeserializer;
import com.example.sipsupporterapp.retrofit.AttachResultDeserializer;
import com.example.sipsupporterapp.retrofit.BankAccountResultDeserializer;
import com.example.sipsupporterapp.retrofit.CaseProductResultDeserializer;
import com.example.sipsupporterapp.retrofit.CaseResultDeserializer;
import com.example.sipsupporterapp.retrofit.CaseTypeResultDeserializer;
import com.example.sipsupporterapp.retrofit.CommentResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerPaymentResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerProductResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerSupportResultDeserializer;
import com.example.sipsupporterapp.retrofit.CustomerUserResultDeserializer;
import com.example.sipsupporterapp.retrofit.DateResultDeserializer;
import com.example.sipsupporterapp.retrofit.NoConnectivityException;
import com.example.sipsupporterapp.retrofit.PaymentResultDeserializer;
import com.example.sipsupporterapp.retrofit.PaymentSubjectResultDeserializer;
import com.example.sipsupporterapp.retrofit.ProductGroupResultDeserializer;
import com.example.sipsupporterapp.retrofit.ProductResultDeserializer;
import com.example.sipsupporterapp.retrofit.RetrofitInstance;
import com.example.sipsupporterapp.retrofit.SipSupporterService;
import com.example.sipsupporterapp.retrofit.SupportEventResultDeserializer;
import com.example.sipsupporterapp.retrofit.UserResultDeserializer;
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

    private SingleLiveEvent<UserResult> changePasswordResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerSupportResult> customerSupportsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerSupportResult> addCustomerSupportResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> customerProductsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> addCustomerProductResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> editCustomerProductResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerProductResult> deleteCustomerProductResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> customerPaymentsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> addCustomerPaymentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> editCustomerPaymentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> deleteCustomerPaymentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> addPaymentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> editPaymentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> deletePaymentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> customerProductAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> customerPaymentAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> customerSupportAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> paymentAttachmentsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerUserResult> customerUsersResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<SupportEventResult> supportEventsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> attachResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> deleteAttachResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<ProductResult> productsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<ProductResult> productInfoResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<ProductResult> addProductResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentSubjectResult> paymentSubjectsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentSubjectResult> paymentSubjectInfoResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> paymentsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AttachResult> attachResultViaAttachIDSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<PaymentResult> paymentsByBankAccountResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerPaymentResult> customerPaymentsByBankAccountResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<BankAccountResult> bankAccountsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<ProductGroupResult> productGroupsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CaseTypeResult> caseTypesResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CaseResult> casesByCaseTypeResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CaseResult> addCaseResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CaseResult> deleteCaseResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CaseResult> editCaseResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CaseResult> closeCaseResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CommentResult> addCommentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<UserResult> userLoginResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CommentResult> commentsByCaseIDResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CommentResult> deleteCommentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CommentResult> editCommentResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<UserResult> usersResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AssignResult> addAssignResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AssignResult> assignsResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AssignResult> editAssignResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<AssignResult> deleteAssignResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CaseProductResult> addCaseProductResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CaseProductResult> caseProductsWithSelectedResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<CustomerResult> customerInfoResultSingleLiveEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<String> noConnectionExceptionHappenSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> timeoutExceptionHappenSingleLiveEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<String> wrongIpAddressSingleLiveEvent = new SingleLiveEvent<>();

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

    public void getSipSupporterServiceUserResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<UserResult>() {
                }.getType(), new UserResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSupporterServiceCustomerResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerResult>() {
                }.getType(), new CustomerResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceCustomerSupportResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerSupportResult>() {
                }.getType(), new CustomerSupportResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceCustomerUserResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerUserResult>() {
                }.getType(), new CustomerUserResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceSupportEventResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<SupportEventResult>() {
                }.getType(), new SupportEventResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceDateResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<DateResult>() {
                }.getType(), new DateResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceCustomerProductResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerProductResult>() {
                }.getType(), new CustomerProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceProductResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<ProductResult>() {
                }.getType(), new ProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceAttachResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AttachResult>() {
                }.getType(), new AttachResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceCustomerPaymentResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new CustomerPaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceBankAccountResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CustomerPaymentResult>() {
                }.getType(), new BankAccountResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServicePaymentResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentResult>() {
                }.getType(), new PaymentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServicePaymentSubjectResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<PaymentSubjectResult>() {
                }.getType(), new PaymentSubjectResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceProductGroups(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<ProductGroupResult>() {
                }.getType(), new ProductGroupResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceCaseTypes(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CaseTypeResult>() {
                }.getType(), new CaseTypeResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceCaseResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CaseResult>() {
                }.getType(), new CaseResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceCommentResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CommentResult>() {
                }.getType(), new CommentResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceAssignResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<AssignResult>() {
                }.getType(), new AssignResultDeserializer(), context).create(SipSupporterService.class);
    }

    public void getSipSupporterServiceCaseProductResult(String baseUrl) {
        RetrofitInstance.getNewBaseUrl(baseUrl);
        sipSupporterService = RetrofitInstance
                .getRI(new TypeToken<CaseProductResult>() {
                }.getType(), new CaseProductResultDeserializer(), context).create(SipSupporterService.class);
    }

    public SingleLiveEvent<DateResult> getDateResultSingleLiveEvent() {
        return dateResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerResult> getCustomersResultSingleLiveEvent() {
        return customersResultSingleLiveEvent;
    }

    public SingleLiveEvent<UserResult> getUserLoginResultSingleLiveEvent() {
        return userLoginResultSingleLiveEvent;
    }

    public SingleLiveEvent<UserResult> getChangePasswordResultSingleLiveEvent() {
        return changePasswordResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerSupportResult> getCustomerSupportsResultSingleLiveEvent() {
        return customerSupportsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerSupportResult> getAddCustomerSupportResultSingleLiveEvent() {
        return addCustomerSupportResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getCustomerProductsResultSingleLiveEvent() {
        return customerProductsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getAddCustomerProductResultSingleLiveEvent() {
        return addCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getEditCustomerProductResultSingleLiveEvent() {
        return editCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerProductResult> getDeleteCustomerProductResultSingleLiveEvent() {
        return deleteCustomerProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getCustomerPaymentsResultSingleLiveEvent() {
        return customerPaymentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getAddCustomerPaymentResultSingleLiveEvent() {
        return addCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getEditCustomerPaymentResultSingleLiveEvent() {
        return editCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerPaymentResult> getDeleteCustomerPaymentResultSingleLiveEvent() {
        return deleteCustomerPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getAddPaymentResultSingleLiveEvent() {
        return addPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getEditPaymentResultSingleLiveEvent() {
        return editPaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getDeletePaymentResultSingleLiveEvent() {
        return deletePaymentResultSingleLiveEvent;
    }

    public SingleLiveEvent<BankAccountResult> getBankAccountsResultSingleLiveEvent() {
        return bankAccountsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getCustomerProductAttachmentsResultSingleLiveEvent() {
        return customerProductAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getCustomerPaymentAttachmentsResultSingleLiveEvent() {
        return customerPaymentAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getCustomerSupportAttachmentsResultSingleLiveEvent() {
        return customerSupportAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getPaymentAttachmentsResultSingleLiveEvent() {
        return paymentAttachmentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerUserResult> getCustomerUsersResultSingleLiveEvent() {
        return customerUsersResultSingleLiveEvent;
    }

    public SingleLiveEvent<SupportEventResult> getSupportEventsResultSingleLiveEvent() {
        return supportEventsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachResultSingleLiveEvent() {
        return attachResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getDeleteAttachResultSingleLiveEvent() {
        return deleteAttachResultSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getProductsResultSingleLiveEvent() {
        return productsResultSingleLiveEvent;
    }

    public SingleLiveEvent<ProductResult> getProductInfoResultSingleLiveEvent() {
        return productInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentSubjectResult> getPaymentSubjectsResultSingleLiveEvent() {
        return paymentSubjectsResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentSubjectResult> getPaymentSubjectInfoResultSingleLiveEvent() {
        return paymentSubjectInfoResultSingleLiveEvent;
    }

    public SingleLiveEvent<PaymentResult> getPaymentsResultSingleLiveEvent() {
        return paymentsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AttachResult> getAttachResultViaAttachIDSingleLiveEvent() {
        return attachResultViaAttachIDSingleLiveEvent;
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

    public SingleLiveEvent<CustomerPaymentResult> getCustomerPaymentsByBankAccountResultSingleLiveEvent() {
        return customerPaymentsByBankAccountResultSingleLiveEvent;
    }

    public SingleLiveEvent<ProductGroupResult> getProductGroupsResultSingleLiveEvent() {
        return productGroupsResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseTypeResult> getCaseTypesResultSingleLiveEvent() {
        return caseTypesResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseResult> getCasesByCaseTypeResultSingleLiveEvent() {
        return casesByCaseTypeResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseResult> getAddCaseResultSingleLiveEvent() {
        return addCaseResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseResult> getDeleteCaseResultSingleLiveEvent() {
        return deleteCaseResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseResult> getEditCaseResultSingleLiveEvent() {
        return editCaseResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseResult> getCloseCaseResultSingleLiveEvent() {
        return closeCaseResultSingleLiveEvent;
    }

    public SingleLiveEvent<CommentResult> getAddCommentResultSingleLiveEvent() {
        return addCommentResultSingleLiveEvent;
    }

    public SingleLiveEvent<CommentResult> getCommentsByCaseIDResultSingleLiveEvent() {
        return commentsByCaseIDResultSingleLiveEvent;
    }

    public SingleLiveEvent<CommentResult> getDeleteCommentResultSingleLiveEvent() {
        return deleteCommentResultSingleLiveEvent;
    }

    public SingleLiveEvent<CommentResult> getEditCommentResultSingleLiveEvent() {
        return editCommentResultSingleLiveEvent;
    }

    public SingleLiveEvent<UserResult> getUsersResultSingleLiveEvent() {
        return usersResultSingleLiveEvent;
    }

    public SingleLiveEvent<AssignResult> getAddAssignResultSingleLiveEvent() {
        return addAssignResultSingleLiveEvent;
    }

    public SingleLiveEvent<AssignResult> getAssignsResultSingleLiveEvent() {
        return assignsResultSingleLiveEvent;
    }

    public SingleLiveEvent<AssignResult> getEditAssignResultSingleLiveEvent() {
        return editAssignResultSingleLiveEvent;
    }

    public SingleLiveEvent<AssignResult> getDeleteAssignResultSingleLiveEvent() {
        return deleteAssignResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseProductResult> getAddCaseProductResultSingleLiveEvent() {
        return addCaseProductResultSingleLiveEvent;
    }

    public SingleLiveEvent<CaseProductResult> getCaseProductsWithSelectedResultSingleLiveEvent() {
        return caseProductsWithSelectedResultSingleLiveEvent;
    }

    public SingleLiveEvent<CustomerResult> getCustomerInfoResultSingleLiveEvent() {
        return customerInfoResultSingleLiveEvent;
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

    public void fetchUserResult(String path, UserResult.UserLoginParameter userLoginParameter) {
        sipSupporterService.login(path, userLoginParameter).enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                if (response.isSuccessful()) {
                    userLoginResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        UserResult userResult = gson.fromJson(response.errorBody().string(), UserResult.class);
                        userLoginResultSingleLiveEvent.setValue(userResult);
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

    public void fetchCustomers(String path, String userLoginKey, String customerName) {
        sipSupporterService.fetchCustomers(path, userLoginKey, customerName).enqueue(new Callback<CustomerResult>() {
            @Override
            public void onResponse(Call<CustomerResult> call, Response<CustomerResult> response) {
                if (response.isSuccessful()) {
                    customersResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerResult customerResult = gson.fromJson(response.errorBody().string(), CustomerResult.class);
                        customersResultSingleLiveEvent.setValue(customerResult);
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
                        changePasswordResultSingleLiveEvent.setValue(userResult);
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

    public void fetchCustomerSupports(String path, String userLoginKey, int customerID) {
        sipSupporterService.fetchCustomerSupports(path, userLoginKey, customerID).enqueue(new Callback<CustomerSupportResult>() {
            @Override
            public void onResponse(Call<CustomerSupportResult> call, Response<CustomerSupportResult> response) {
                if (response.isSuccessful()) {
                    customerSupportsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerSupportResult customerSupportResult = gson.fromJson(response.errorBody().string(), CustomerSupportResult.class);
                        customerSupportsResultSingleLiveEvent.setValue(customerSupportResult);
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


    public void fetchUsers(String path, String userLoginKey, int customerID) {
        sipSupporterService.fetchCustomerUsers(path, userLoginKey, customerID).enqueue(new Callback<CustomerUserResult>() {
            @Override
            public void onResponse(Call<CustomerUserResult> call, Response<CustomerUserResult> response) {
                if (response.isSuccessful()) {
                    customerUsersResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerUserResult customerUserResult = gson.fromJson(response.errorBody().string(), CustomerUserResult.class);
                        customerUsersResultSingleLiveEvent.setValue(customerUserResult);
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


    public void fetchSupportEventsResult(String path, String userLoginKey) {
        sipSupporterService.fetchSupportEvents(path, userLoginKey).enqueue(new Callback<SupportEventResult>() {
            @Override
            public void onResponse(Call<SupportEventResult> call, Response<SupportEventResult> response) {
                if (response.isSuccessful()) {
                    supportEventsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        SupportEventResult supportEventResult = gson.fromJson(response.errorBody().string(), SupportEventResult.class);
                        supportEventsResultSingleLiveEvent.setValue(supportEventResult);
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

    public void addCustomerSupport(String path, String userLoginKey, CustomerSupportResult.CustomerSupportInfo customerSupportInfo) {
        sipSupporterService.addCustomerSupport(path, userLoginKey, customerSupportInfo).enqueue(new Callback<CustomerSupportResult>() {
            @Override
            public void onResponse(Call<CustomerSupportResult> call, Response<CustomerSupportResult> response) {
                if (response.isSuccessful()) {
                    addCustomerSupportResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerSupportResult customerSupportResult = gson.fromJson(response.errorBody().string(), CustomerSupportResult.class);
                        addCustomerSupportResultSingleLiveEvent.setValue(customerSupportResult);
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

    public void fetchDate(String path, String userLoginKey) {
        sipSupporterService.fetchDate(path, userLoginKey).enqueue(new Callback<DateResult>() {
            @Override
            public void onResponse(Call<DateResult> call, Response<DateResult> response) {
                dateResultSingleLiveEvent.setValue(response.body());
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

    public void fetchCustomerProducts(String path, String userLoginKey, int customerID) {
        sipSupporterService.fetchCustomerProducts(path, userLoginKey, customerID).enqueue(new Callback<CustomerProductResult>() {
            @Override
            public void onResponse(Call<CustomerProductResult> call, Response<CustomerProductResult> response) {
                if (response.isSuccessful()) {
                    customerProductsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerProductResult customerProductResult = gson.fromJson(response.errorBody().string(), CustomerProductResult.class);
                        customerProductsResultSingleLiveEvent.setValue(customerProductResult);
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

    public void fetchCustomerProducts(String path, String userLoginKey) {
        sipSupporterService.fetchProducts(path, userLoginKey).enqueue(new Callback<ProductResult>() {
            @Override
            public void onResponse(Call<ProductResult> call, Response<ProductResult> response) {
                if (response.isSuccessful()) {
                    productsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        ProductResult productResult = gson.fromJson(response.errorBody().string(), ProductResult.class);
                        productsResultSingleLiveEvent.setValue(productResult);
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

    public void addCustomerProduct(String path, String userLoginKey, CustomerProductResult.CustomerProductInfo customerProductInfo) {
        sipSupporterService.addCustomerProduct(path, userLoginKey, customerProductInfo).enqueue(new Callback<CustomerProductResult>() {
            @Override
            public void onResponse(Call<CustomerProductResult> call, Response<CustomerProductResult> response) {
                if (response.isSuccessful()) {
                    addCustomerProductResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerProductResult customerProductResult = gson.fromJson(response.errorBody().string(), CustomerProductResult.class);
                        addCustomerProductResultSingleLiveEvent.setValue(customerProductResult);
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
                        productInfoResultSingleLiveEvent.setValue(productResult);
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
                        deleteCustomerProductResultSingleLiveEvent.setValue(customerProductResult);
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

    public void editCustomerProduct(String path, String userLoginKey, CustomerProductResult.CustomerProductInfo customerProductInfo) {
        sipSupporterService.editCustomerProduct(path, userLoginKey, customerProductInfo).enqueue(new Callback<CustomerProductResult>() {
            @Override
            public void onResponse(Call<CustomerProductResult> call, Response<CustomerProductResult> response) {
                if (response.isSuccessful()) {
                    editCustomerProductResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerProductResult customerProductResult = gson.fromJson(response.errorBody().string(), CustomerProductResult.class);
                        editCustomerProductResultSingleLiveEvent.setValue(customerProductResult);
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

    public void attach(String path, String userLoginKey, AttachResult.AttachInfo attachInfo) {
        sipSupporterService.attach(path, userLoginKey, attachInfo).enqueue(new Callback<AttachResult>() {
            @Override
            public void onResponse(Call<AttachResult> call, Response<AttachResult> response) {
                if (response.isSuccessful()) {
                    attachResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AttachResult attachResult = gson.fromJson(response.errorBody().string(), AttachResult.class);
                        attachResultSingleLiveEvent.setValue(attachResult);
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

    public void fetchCustomerPaymentsResult(String path, String userLoginKey, int customerID) {
        sipSupporterService.fetchCustomerPayments(path, userLoginKey, customerID).enqueue(new Callback<CustomerPaymentResult>() {
            @Override
            public void onResponse(Call<CustomerPaymentResult> call, Response<CustomerPaymentResult> response) {
                if (response.isSuccessful()) {
                    customerPaymentsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerPaymentResult customerPaymentResult = gson.fromJson(response.errorBody().string(), CustomerPaymentResult.class);
                        customerPaymentsResultSingleLiveEvent.setValue(customerPaymentResult);
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
                        customerPaymentAttachmentsResultSingleLiveEvent.setValue(attachResult);
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

    public void addCustomerPayment(String path, String userLoginKey, CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
        sipSupporterService.addCustomerPayment(path, userLoginKey, customerPaymentInfo).enqueue(new Callback<CustomerPaymentResult>() {
            @Override
            public void onResponse(Call<CustomerPaymentResult> call, Response<CustomerPaymentResult> response) {
                if (response.isSuccessful()) {
                    addCustomerPaymentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerPaymentResult customerPaymentResult = gson.fromJson(response.errorBody().string(), CustomerPaymentResult.class);
                        addCustomerPaymentResultSingleLiveEvent.setValue(customerPaymentResult);
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

    public void editCustomerPayment(String path, String userLoginKey, CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo) {
        sipSupporterService.editCustomerPayment(path, userLoginKey, customerPaymentInfo).enqueue(new Callback<CustomerPaymentResult>() {
            @Override
            public void onResponse(Call<CustomerPaymentResult> call, Response<CustomerPaymentResult> response) {
                if (response.isSuccessful()) {
                    editCustomerPaymentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerPaymentResult customerPaymentResult = gson.fromJson(response.errorBody().string(), CustomerPaymentResult.class);
                        editCustomerPaymentResultSingleLiveEvent.setValue(customerPaymentResult);
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

    public void deleteCustomerPayment(String path, String userLoginKey, int customerPaymentID) {
        sipSupporterService.deleteCustomerPayment(path, userLoginKey, customerPaymentID).enqueue(new Callback<CustomerPaymentResult>() {
            @Override
            public void onResponse(Call<CustomerPaymentResult> call, Response<CustomerPaymentResult> response) {
                if (response.isSuccessful()) {
                    deleteCustomerPaymentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerPaymentResult customerPaymentResult = gson.fromJson(response.errorBody().string(), CustomerPaymentResult.class);
                        deleteCustomerPaymentResultSingleLiveEvent.setValue(customerPaymentResult);
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
                        customerProductAttachmentsResultSingleLiveEvent.setValue(attachResult);
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
                        customerSupportAttachmentsResultSingleLiveEvent.setValue(attachResult);
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
                        attachResultViaAttachIDSingleLiveEvent.setValue(attachResult);
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
                        paymentsResultSingleLiveEvent.setValue(paymentResult);
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

    public void editPayment(String path, String userLoginKey, PaymentResult.PaymentInfo paymentInfo) {
        sipSupporterService.editPayment(path, userLoginKey, paymentInfo).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                if (response.isSuccessful()) {
                    editPaymentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentResult paymentResult = gson.fromJson(response.errorBody().string(), PaymentResult.class);
                        editPaymentResultSingleLiveEvent.setValue(paymentResult);
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

    public void deletePayment(String path, String userLoginKey, int paymentID) {
        sipSupporterService.deletePayment(path, userLoginKey, paymentID).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                if (response.isSuccessful()) {
                    deletePaymentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentResult paymentResult = gson.fromJson(response.errorBody().string(), PaymentResult.class);
                        deletePaymentResultSingleLiveEvent.setValue(paymentResult);
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
                        paymentSubjectsResultSingleLiveEvent.setValue(paymentSubjectResult);
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

    public void addPayment(String path, String userLoginKey, PaymentResult.PaymentInfo paymentInfo) {
        sipSupporterService.addPayment(path, userLoginKey, paymentInfo).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                if (response.isSuccessful()) {
                    addPaymentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        PaymentResult paymentResult = gson.fromJson(response.errorBody().string(), PaymentResult.class);
                        addPaymentResultSingleLiveEvent.setValue(paymentResult);
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
                        deleteAttachResultSingleLiveEvent.setValue(attachResult);
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
                        paymentAttachmentsResultSingleLiveEvent.setValue(attachResult);
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
                        PaymentSubjectResult paymentSubjectResult = gson.fromJson(response.errorBody().string(), PaymentSubjectResult.class);
                        paymentSubjectInfoResultSingleLiveEvent.setValue(paymentSubjectResult);
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
                        customerPaymentsByBankAccountResultSingleLiveEvent.setValue(customerPaymentResult);
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
                        bankAccountsResultSingleLiveEvent.setValue(bankAccountResult);
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

    public void fetchProductGroups(String path, String userLoginKey) {
        sipSupporterService.fetchProductGroups(path, userLoginKey).enqueue(new Callback<ProductGroupResult>() {
            @Override
            public void onResponse(Call<ProductGroupResult> call, Response<ProductGroupResult> response) {
                if (response.isSuccessful()) {
                    productGroupsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        ProductGroupResult productGroupResult = gson.fromJson(response.errorBody().string(), ProductGroupResult.class);
                        productGroupsResultSingleLiveEvent.setValue(productGroupResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductGroupResult> call, Throwable t) {
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

    public void fetchCaseTypes(String path, String userLoginKey) {
        sipSupporterService.fetchCaseTypes(path, userLoginKey).enqueue(new Callback<CaseTypeResult>() {
            @Override
            public void onResponse(Call<CaseTypeResult> call, Response<CaseTypeResult> response) {
                if (response.isSuccessful()) {
                    caseTypesResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CaseTypeResult caseTypeResult = gson.fromJson(response.errorBody().string(), CaseTypeResult.class);
                        caseTypesResultSingleLiveEvent.setValue(caseTypeResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CaseTypeResult> call, Throwable t) {
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

    public void fetchCasesByCaseType(String path, String userLoginKey, int caseTypeID, String search, boolean showAll) {
        sipSupporterService.fetchCasesByCaseType(path, userLoginKey, caseTypeID, search, showAll).enqueue(new Callback<CaseResult>() {
            @Override
            public void onResponse(Call<CaseResult> call, Response<CaseResult> response) {
                if (response.isSuccessful()) {
                    casesByCaseTypeResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CaseResult caseResult = gson.fromJson(response.errorBody().string(), CaseResult.class);
                        casesByCaseTypeResultSingleLiveEvent.setValue(caseResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CaseResult> call, Throwable t) {
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

    public void addCase(String path, String userLoginKey, CaseResult.CaseInfo caseInfo) {
        sipSupporterService.addCase(path, userLoginKey, caseInfo).enqueue(new Callback<CaseResult>() {
            @Override
            public void onResponse(Call<CaseResult> call, Response<CaseResult> response) {
                if (response.isSuccessful()) {
                    addCaseResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CaseResult caseResult = gson.fromJson(response.errorBody().string(), CaseResult.class);
                        addCaseResultSingleLiveEvent.setValue(caseResult);
                    } catch (IOException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CaseResult> call, Throwable t) {
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

    public void deleteCase(String path, String userLoginKey, int caseID) {
        sipSupporterService.deleteCase(path, userLoginKey, caseID).enqueue(new Callback<CaseResult>() {
            @Override
            public void onResponse(Call<CaseResult> call, Response<CaseResult> response) {
                if (response.isSuccessful()) {
                    deleteCaseResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CaseResult caseResult = gson.fromJson(response.errorBody().string(), CaseResult.class);
                        deleteCaseResultSingleLiveEvent.setValue(caseResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CaseResult> call, Throwable t) {
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

    public void editCase(String path, String userLoginKey, CaseResult.CaseInfo caseInfo) {
        sipSupporterService.editCase(path, userLoginKey, caseInfo).enqueue(new Callback<CaseResult>() {
            @Override
            public void onResponse(Call<CaseResult> call, Response<CaseResult> response) {
                if (response.isSuccessful()) {
                    editCaseResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CaseResult caseResult = gson.fromJson(response.errorBody().string(), CaseResult.class);
                        editCaseResultSingleLiveEvent.setValue(caseResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CaseResult> call, Throwable t) {
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

    public void closeCase(String path, String userLoginKey, CaseResult.CaseInfo caseInfo) {
        sipSupporterService.closeCase(path, userLoginKey, caseInfo).enqueue(new Callback<CaseResult>() {
            @Override
            public void onResponse(Call<CaseResult> call, Response<CaseResult> response) {
                if (response.isSuccessful()) {
                    closeCaseResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CaseResult caseResult = gson.fromJson(response.errorBody().string(), CaseResult.class);
                        closeCaseResultSingleLiveEvent.setValue(caseResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CaseResult> call, Throwable t) {
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

    public void addComment(String path, String userLoginKey, CommentResult.CommentInfo commentInfo) {
        sipSupporterService.addComment(path, userLoginKey, commentInfo).enqueue(new Callback<CommentResult>() {
            @Override
            public void onResponse(Call<CommentResult> call, Response<CommentResult> response) {
                if (response.isSuccessful()) {
                    addCommentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CommentResult commentResult = gson.fromJson(response.errorBody().string(), CommentResult.class);
                        addCommentResultSingleLiveEvent.setValue(commentResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentResult> call, Throwable t) {
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

    public void fetchCommentsByCaseID(String path, String userLoginKey, int caseID) {
        sipSupporterService.fetchCommentsByCaseID(path, userLoginKey, caseID).enqueue(new Callback<CommentResult>() {
            @Override
            public void onResponse(Call<CommentResult> call, Response<CommentResult> response) {
                if (response.isSuccessful()) {
                    commentsByCaseIDResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CommentResult commentResult = gson.fromJson(response.errorBody().string(), CommentResult.class);
                        commentsByCaseIDResultSingleLiveEvent.setValue(commentResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentResult> call, Throwable t) {
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

    public void deleteComment(String path, String userLoginKey, int commentID) {
        sipSupporterService.deleteComment(path, userLoginKey, commentID).enqueue(new Callback<CommentResult>() {
            @Override
            public void onResponse(Call<CommentResult> call, Response<CommentResult> response) {
                if (response.isSuccessful()) {
                    deleteCommentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CommentResult commentResult = gson.fromJson(response.errorBody().string(), CommentResult.class);
                        deleteCommentResultSingleLiveEvent.setValue(commentResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentResult> call, Throwable t) {
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

    public void editComment(String path, String userLoginKey, CommentResult.CommentInfo commentInfo) {
        sipSupporterService.editComment(path, userLoginKey, commentInfo).enqueue(new Callback<CommentResult>() {
            @Override
            public void onResponse(Call<CommentResult> call, Response<CommentResult> response) {
                if (response.isSuccessful()) {
                    editCommentResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CommentResult commentResult = gson.fromJson(response.errorBody().string(), CommentResult.class);
                        editCommentResultSingleLiveEvent.setValue(commentResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentResult> call, Throwable t) {
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

    public void fetchUsers(String path, String userLoginKey) {
        sipSupporterService.fetchUsers(path, userLoginKey).enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                if (response.isSuccessful()) {
                    usersResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        UserResult userResult = gson.fromJson(response.errorBody().string(), UserResult.class);
                        usersResultSingleLiveEvent.setValue(userResult);
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

    public void addAssign(String path, String userLoginKey, AssignResult.AssignInfo assignInfo) {
        sipSupporterService.addAssign(path, userLoginKey, assignInfo).enqueue(new Callback<AssignResult>() {
            @Override
            public void onResponse(Call<AssignResult> call, Response<AssignResult> response) {
                if (response.isSuccessful()) {
                    addAssignResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AssignResult assignResult = gson.fromJson(response.errorBody().string(), AssignResult.class);
                        addAssignResultSingleLiveEvent.setValue(assignResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AssignResult> call, Throwable t) {
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

    public void fetchAssigns(String path, String userLoginKey, int caseID) {
        sipSupporterService.fetchAssigns(path, userLoginKey, caseID).enqueue(new Callback<AssignResult>() {
            @Override
            public void onResponse(Call<AssignResult> call, Response<AssignResult> response) {
                if (response.isSuccessful()) {
                    assignsResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AssignResult assignResult = gson.fromJson(response.errorBody().string(), AssignResult.class);
                        assignsResultSingleLiveEvent.setValue(assignResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AssignResult> call, Throwable t) {
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

    public void editAssign(String path, String userLoginKey, AssignResult.AssignInfo assignInfo) {
        sipSupporterService.editAssign(path, userLoginKey, assignInfo).enqueue(new Callback<AssignResult>() {
            @Override
            public void onResponse(Call<AssignResult> call, Response<AssignResult> response) {
                if (response.isSuccessful()) {
                    editAssignResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AssignResult assignResult = gson.fromJson(response.errorBody().string(), AssignResult.class);
                        editAssignResultSingleLiveEvent.setValue(assignResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AssignResult> call, Throwable t) {
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

    public void deleteAssign(String path, String userLoginKey, int assignID) {
        sipSupporterService.deleteAssign(path, userLoginKey, assignID).enqueue(new Callback<AssignResult>() {
            @Override
            public void onResponse(Call<AssignResult> call, Response<AssignResult> response) {
                if (response.isSuccessful()) {
                    deleteAssignResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        AssignResult assignResult = gson.fromJson(response.errorBody().string(), AssignResult.class);
                        deleteAssignResultSingleLiveEvent.setValue(assignResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AssignResult> call, Throwable t) {
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

    public void addCaseProduct(String path, String userLoginKey, CaseProductResult.CaseProductInfo caseProductInfo) {
        sipSupporterService.addCaseProduct(path, userLoginKey, caseProductInfo).enqueue(new Callback<CaseProductResult>() {
            @Override
            public void onResponse(Call<CaseProductResult> call, Response<CaseProductResult> response) {
                if (response.isSuccessful()) {
                    addCaseProductResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CaseProductResult caseProductResult = gson.fromJson(response.errorBody().string(), CaseProductResult.class);
                        addCaseProductResultSingleLiveEvent.setValue(caseProductResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CaseProductResult> call, Throwable t) {
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

    public void fetchCaseProductsWithSelected(String path, String userLoginKey, int caseID) {
        sipSupporterService.fetchCaseProductsWithSelected(path, userLoginKey, caseID).enqueue(new Callback<CaseProductResult>() {
            @Override
            public void onResponse(Call<CaseProductResult> call, Response<CaseProductResult> response) {
                if (response.isSuccessful()) {
                    caseProductsWithSelectedResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CaseProductResult caseProductResult = gson.fromJson(response.errorBody().string(), CaseProductResult.class);
                        caseProductsWithSelectedResultSingleLiveEvent.setValue(caseProductResult);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CaseProductResult> call, Throwable t) {
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

    public void fetchCustomerInfo(String path, String userLoginKey, int customerID) {
        sipSupporterService.fetchCustomerInfo(path, userLoginKey, customerID).enqueue(new Callback<CustomerResult>() {
            @Override
            public void onResponse(Call<CustomerResult> call, Response<CustomerResult> response) {
                if (response.isSuccessful()) {
                    customerInfoResultSingleLiveEvent.setValue(response.body());
                } else {
                    try {
                        Gson gson = new Gson();
                        CustomerResult customerResult = gson.fromJson(response.errorBody().string(), CustomerResult.class);
                        customerInfoResultSingleLiveEvent.setValue(customerResult);
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
}
