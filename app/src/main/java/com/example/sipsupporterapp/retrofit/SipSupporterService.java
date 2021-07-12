package com.example.sipsupporterapp.retrofit;

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
import com.example.sipsupporterapp.model.InvoiceDetailsResult;
import com.example.sipsupporterapp.model.InvoiceResult;
import com.example.sipsupporterapp.model.PaymentResult;
import com.example.sipsupporterapp.model.PaymentSubjectResult;
import com.example.sipsupporterapp.model.ProductGroupResult;
import com.example.sipsupporterapp.model.ProductResult;
import com.example.sipsupporterapp.model.SupportEventResult;
import com.example.sipsupporterapp.model.UserResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SipSupporterService {

    @GET("{path}")
    Call<DateResult> fetchDate(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @POST("{path}")
    Call<UserResult> login(@Path("path") String path, @Body UserResult.UserLoginParameter userLoginParameter);

    @POST("{path}")
    Call<UserResult> changePassword(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body String newPassword);

    @GET("{path}")
    Call<CustomerResult> fetchCustomers(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerName") String customerName);

    @GET("{path}")
    Call<CustomerSupportResult> fetchCustomerSupports(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerID") int customerID);

    @POST("{path}")
    Call<CustomerSupportResult> addCustomerSupport(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerSupportResult.CustomerSupportInfo customerSupportInfo);

    @GET("{path}")
    Call<SupportEventResult> fetchSupportEvents(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @GET("{path}")
    Call<CustomerUserResult> fetchCustomerUsers(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerID") int customerID);

    @GET("{path}")
    Call<CustomerProductResult> fetchCustomerProducts(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerID") int customerID);

    @GET("{path}")
    Call<ProductResult> fetchProducts(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @GET("{path}")
    Call<ProductResult> fetchProductInfo(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("productID") int productID);

    @POST("{path}")
    Call<CustomerProductResult> addCustomerProduct(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerProductResult.CustomerProductInfo customerProductInfo);

    @PUT("{path}")
    Call<CustomerProductResult> editCustomerProduct(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerProductResult.CustomerProductInfo customerProductInfo);

    @DELETE("{path}")
    Call<CustomerProductResult> deleteCustomerProduct(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerProductID") int customerProductID);

    @GET("{path}")
    Call<CustomerPaymentResult> fetchCustomerPayments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerID") int customerID);

    @POST("{path}")
    Call<CustomerPaymentResult> addCustomerPayment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo);

    @PUT("{path}")
    Call<CustomerPaymentResult> editCustomerPayment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CustomerPaymentResult.CustomerPaymentInfo customerPaymentInfo);

    @DELETE("{path}")
    Call<CustomerPaymentResult> deleteCustomerPayment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerPaymentID") int customerPaymentID);

    @GET("{path}")
    Call<AttachResult> fetchCustomerPaymentAttachments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerPaymentID") int paymentID, @Query("LoadFileData") boolean LoadFileData);

    @GET("{path}")
    Call<AttachResult> fetchCustomerProductAttachments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerProductID") int customerProductID, @Query("LoadFileData") boolean LoadFileData);

    @GET("{path}")
    Call<AttachResult> fetchCustomerSupportAttachments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerSupportID") int customerSupportID, @Query("LoadFileData") boolean LoadFileData);

    @GET("{path}")
    Call<AttachResult> fetchPaymentAttachments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("paymentID") int paymentID, @Query("LoadFileData") boolean LoadFileData);

    @POST("{path}")
    Call<AttachResult> attach(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body AttachResult.AttachInfo attachInfo);

    @DELETE("{path}")
    Call<AttachResult> deleteAttach(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("attachID") int attachID);

    @GET("{path}")
    Call<AttachResult> fetchAttachInfo(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("attachID") int attachID, @Query("LoadFileData") boolean LoadFileData);

    @GET("{path}")
    Call<PaymentSubjectResult> fetchPaymentSubjects(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @GET("{path}")
    Call<PaymentResult> fetchPayments(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("bankAccountID") int bankAccountID);

    @GET("{path}")
    Call<PaymentSubjectResult> fetchPaymentInfo(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("paymentSubjectID") int paymentSubjectID);

    @POST("{path}")
    Call<PaymentResult> addPayment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body PaymentResult.PaymentInfo paymentInfo);

    @PUT("{path}")
    Call<PaymentResult> editPayment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body PaymentResult.PaymentInfo paymentInfo);

    @DELETE("{path}")
    Call<PaymentResult> deletePayment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("paymentID") int paymentID);

    @GET("{path}")
    Call<CustomerPaymentResult> fetchCustomerPaymentsByBankAccount(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("bankAccountID") int bankAccountID);

    @GET("{path}")
    Call<BankAccountResult> fetchBankAccounts(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @GET("{path}")
    Call<ProductGroupResult> fetchProductGroups(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @GET("{path}")
    Call<CaseTypeResult> fetchCaseTypes(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @GET("{path}")
    Call<CaseResult> fetchCasesByCaseType(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("caseTypeID") int caseTypeID, @Query("search") String search, @Query("showAll") boolean showAll);

    @POST("{path}")
    Call<CaseResult> addCase(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CaseResult.CaseInfo caseInfo);

    @DELETE("{path}")
    Call<CaseResult> deleteCase(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("caseID") int caseID);

    @PUT("{path}")
    Call<CaseResult> editCase(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CaseResult.CaseInfo caseInfo);

    @PUT("{path}")
    Call<CaseResult> closeCase(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CaseResult.CaseInfo caseInfo);

    @POST("{path}")
    Call<CommentResult> addComment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CommentResult.CommentInfo commentInfo);

    @GET("{path}")
    Call<CommentResult> fetchCommentsByCaseID(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("caseID") int caseID);

    @DELETE("{path}")
    Call<CommentResult> deleteComment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("commentID") int commentID);

    @PUT("{path}")
    Call<CommentResult> editComment(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CommentResult.CommentInfo commentInfo);

    @GET("{path}")
    Call<UserResult> fetchUsers(@Path("path") String path, @Header("userLoginKey") String userLoginKey);

    @POST("{path}")
    Call<AssignResult> addAssign(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body AssignResult.AssignInfo assignInfo);

    @GET("{path}")
    Call<AssignResult> fetchAssigns(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("caseID") int caseID);

    @PUT("{path}")
    Call<AssignResult> editAssign(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body AssignResult.AssignInfo assignInfo);

    @DELETE("{path}")
    Call<AssignResult> deleteAssign(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("assignID") int assignID);

    @POST("{path}")
    Call<CaseProductResult> addCaseProduct(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body CaseProductResult.CaseProductInfo caseProductInfo);

    @GET("{path}")
    Call<CaseProductResult> fetchCaseProductsWithSelected(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("caseID") int caseID);

    @DELETE("{path}")
    Call<CaseProductResult> deleteCaseProduct(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("caseProductID") int caseProductID);

    @GET("{path}")
    Call<CustomerResult> fetchCustomerInfo(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("customerID") int customerID);

    @GET("{path}")
    Call<InvoiceResult> fetchInvoiceInfoByCaseID(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("caseID") int caseID);

    @POST("{path}")
    Call<InvoiceResult> addInvoice(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body InvoiceResult.InvoiceInfo invoiceInfo);

    @GET("{path}")
    Call<CaseProductResult> fetchCaseProducts(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("caseID") int caseID);

    @GET("{path}")
    Call<InvoiceDetailsResult> fetchInvoiceDetails(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("invoiceID") int invoiceID);

    @POST("{path}")
    Call<InvoiceDetailsResult> addInvoiceDetails(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body InvoiceDetailsResult.InvoiceDetailsInfo invoiceDetailsInfo);

    @PUT("{path}")
    Call<InvoiceDetailsResult> editInvoiceDetails(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Body InvoiceDetailsResult.InvoiceDetailsInfo invoiceDetailsInfo);

    @DELETE("{path}")
    Call<InvoiceDetailsResult> deleteInvoiceDetails(@Path("path") String path, @Header("userLoginKey") String userLoginKey, @Query("invoiceDetailsID") int invoiceDetailsID);
}
