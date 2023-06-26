package vn.vnpay.dbinterface.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vnpay.dbinterface.entity.BillProvider;
import vn.vnpay.dbinterface.entity.BillService;
import vn.vnpay.dbinterface.entity.BillSubProvider;
import vn.vnpay.dbinterface.repository.BillProviderRepository;
import vn.vnpay.dbinterface.repository.BillServiceRepository;
import vn.vnpay.dbinterface.repository.BillSubProviderRepository;

import java.util.ArrayList;

@Service
@Slf4j
public class SMEBillServices {

    @Autowired
    BillServiceRepository billServiceRepository;

    @Autowired
    BillProviderRepository billProviderRepository;

    @Autowired
    BillSubProviderRepository billSubProviderRepository;

    public ArrayList<BillService> getAllBillServices() {
        //    return billServiceRepository.findAll(Sort.by(Sort.Direction.ASC, "orderNumber"));
        return billServiceRepository.findAllByStatusOrderByOrderNumber("1");
    }

    public ArrayList<BillProvider> getProviders(
            String serviceCode) {
        ArrayList<BillProvider> providers =
                billProviderRepository.findAllByBillServiceCodeAndStatusOrderByOrderNumber(
                        serviceCode, "1");
        providers.parallelStream()
                .forEach(
                        provider -> {
                            try {
                                // sme's provider tag
                                provider.setCompanyCode("SME");
                                // get all sub provider
                                ArrayList<BillSubProvider> subProviders =
                                        billSubProviderRepository.findAllByBillProviderCodeAndStatus(
                                                provider.getBillProviderCode(), "1");
                                provider.setSubProviders(subProviders);
                                // put data to local cache provider
//                mapBillProvider.put(serviceCode + "@" + provider.getBillProviderCode(), provider);
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                        });
        return providers;
    }
}
