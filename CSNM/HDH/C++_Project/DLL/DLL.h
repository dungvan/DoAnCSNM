#include <Windows.h>
#include <fstream>

__declspec(dllexport) LRESULT __stdcall CALLBACK FillKeyboard(int nCode, WPARAM wParam, LPARAM lParam);
__declspec(dllexport) BOOL InstallHook();