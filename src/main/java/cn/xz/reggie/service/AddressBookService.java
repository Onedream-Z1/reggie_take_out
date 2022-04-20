package cn.xz.reggie.service;

import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.AddressBook;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    R<AddressBook> saveAddr(AddressBook addressBook);

    R<List<AddressBook>> listAddr(AddressBook addressBook);

    R<AddressBook> setDefaultAddr(AddressBook addressBook);

    R<AddressBook> getDeaultAddr();

    R<AddressBook> getAddrById(Long id);

    R<String> updateAddr(AddressBook addressBook);
}
