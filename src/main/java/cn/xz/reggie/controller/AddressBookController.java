package cn.xz.reggie.controller;

import cn.xz.reggie.common.R;
import cn.xz.reggie.entity.AddressBook;
import cn.xz.reggie.service.impl.AddressBookServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookServiceImpl addressBookService;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        //log.info("addressBook={}",addressBook.toString());
        return addressBookService.saveAddr(addressBook);
    }

    /**
     * 查询用户全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        return addressBookService.listAddr(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default")
    public R<AddressBook> setDefaultAddr(@RequestBody AddressBook addressBook){
        return addressBookService.setDefaultAddr(addressBook);
    }

    /**
     * 查询默认地址
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        return addressBookService.getDeaultAddr();
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id){
        return addressBookService.getAddrById(id);
    }

    /**
     * 修改用户地址
     */
    @PutMapping
    public R<String> updateAddr(@RequestBody AddressBook addressBook){
        return addressBookService.updateAddr(addressBook);
    }

    /**
     * 删除用户地址
     */
    @DeleteMapping
    public R<String> deleteAddr(@RequestParam Long ids){
        addressBookService.removeById(ids);
        return R.success("删除地址成功");
    }
}
