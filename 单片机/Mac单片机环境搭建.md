## 安装编译器

```
brew install sdcc
```

## USB转串口
用于连接单片机

http://www.wch.cn/downloads/CH341SER_MAC_ZIP.html


## 单片机烧录程序
推荐使用 `python3`
```
pip3 install stcgal
```
```
➜  ~ stcgal -h
usage: stcgal [-h] [-a] [-r RESETCMD]
              [-P {stc89,stc12a,stc12b,stc12,stc15a,stc15,stc8,usb15,auto}]
              [-p PORT] [-b BAUD] [-l HANDSHAKE] [-o OPTION] [-t TRIM] [-D]
              [-V]
              [code_image] [eeprom_image]

stcgal 1.6 - an STC MCU ISP flash tool
(C) 2014-2018 Grigori Goronzy and others
https://github.com/grigorig/stcgal

positional arguments:
  code_image            code segment file to flash (BIN/HEX)
  eeprom_image          eeprom segment file to flash (BIN/HEX)

optional arguments:
  -h, --help            show this help message and exit
  -a, --autoreset       cycle power automatically by asserting DTR
  -r RESETCMD, --resetcmd RESETCMD
                        shell command for board power-cycling (instead of DTR
                        assertion)
  -P {stc89,stc12a,stc12b,stc12,stc15a,stc15,stc8,usb15,auto}, --protocol {stc89,stc12a,stc12b,stc12,stc15a,stc15,stc8,usb15,auto}
                        protocol version (default: auto)
  -p PORT, --port PORT  serial port device
  -b BAUD, --baud BAUD  transfer baud rate (default: 19200)
  -l HANDSHAKE, --handshake HANDSHAKE
                        handshake baud rate (default: 2400)
  -o OPTION, --option OPTION
                        set option (can be used multiple times, see
                        documentation)
  -t TRIM, --trim TRIM  RC oscillator frequency in kHz (STC15+ series only)
  -D, --debug           enable debug output
  -V, --version         print version info and exit
```


## link

教程：https://www.jianshu.com/p/88a714042cd4

驱动安装：http://www.wch.cn/download/CH341SER_MAC_ZIP.html

烧入工具：https://github.com/grigorig/stcgal
