/**************************************************************************/
/* LabWindows/CVI User Interface Resource (UIR) Include File              */
/*                                                                        */
/* WARNING: Do not add to, delete from, or otherwise modify the contents  */
/*          of this include file.                                         */
/**************************************************************************/

#include <userint.h>

#ifdef __cplusplus
    extern "C" {
#endif

     /* Panels and Controls: */

#define  PANEL                            1       /* callback function: OnMainPanel */
#define  PANEL_GRAPH                      2       /* control type: graph, callback function: (none) */
#define  PANEL_NUMERIC                    3       /* control type: numeric, callback function: (none) */
#define  PANEL_NUMERIC_2                  4       /* control type: numeric, callback function: (none) */
#define  PANEL_Index_1                    5       /* control type: numeric, callback function: (none) */
#define  PANEL_Index_2                    6       /* control type: numeric, callback function: (none) */
#define  PANEL_NUMERIC_3                  7       /* control type: numeric, callback function: (none) */
#define  PANEL_COMMANDBUTTON              8       /* control type: command, callback function: OnLoadButtonCB */
#define  PANEL_NUMERIC_4                  9       /* control type: numeric, callback function: (none) */
#define  PANEL_NUMERIC_5                  10      /* control type: numeric, callback function: (none) */
#define  PANEL_NUMERIC_6                  11      /* control type: numeric, callback function: (none) */
#define  PANEL_GRAPH_2                    12      /* control type: graph, callback function: (none) */
#define  PANEL_GRAPH_3                    13      /* control type: graph, callback function: (none) */
#define  PANEL_NUMERICSLIDE               14      /* control type: scale, callback function: (none) */
#define  PANEL_RINGSLIDE                  15      /* control type: slide, callback function: (none) */
#define  PANEL_COMMANDBUTTON_2            16      /* control type: command, callback function: OnNext */
#define  PANEL_DECORATION                 17      /* control type: deco, callback function: (none) */
#define  PANEL_COMMANDBUTTON_3            18      /* control type: command, callback function: OnPrev */
#define  PANEL_StartPoint                 19      /* control type: numeric, callback function: (none) */
#define  PANEL_StopPoint                  20      /* control type: numeric, callback function: (none) */
#define  PANEL_DECORATION_2               21      /* control type: deco, callback function: (none) */
#define  PANEL_Buton_Filtrare             22      /* control type: command, callback function: OnFiltrare */
#define  PANEL_SwitchPanel                23      /* control type: binary, callback function: OnSwitch */
#define  PANEL_TEXTMSG                    24      /* control type: textMsg, callback function: (none) */

#define  PANEL_2                          2       /* callback function: OnSecondPanel */
#define  PANEL_2_SwitchPanel              2       /* control type: binary, callback function: OnSwitch */
#define  PANEL_2_GRAPH                    3       /* control type: graph, callback function: (none) */
#define  PANEL_2_RING_NumPoint            4       /* control type: ring, callback function: (none) */
#define  PANEL_2_GRAPH_2                  5       /* control type: graph, callback function: (none) */
#define  PANEL_2_GRAPH_3                  6       /* control type: graph, callback function: (none) */
#define  PANEL_2_GRAPH_4                  7       /* control type: graph, callback function: (none) */
#define  PANEL_2_GRAPH_5                  8       /* control type: graph, callback function: (none) */
#define  PANEL_2_GRAPH_6                  9       /* control type: graph, callback function: (none) */
#define  PANEL_2_TIMER                    10      /* control type: timer, callback function: OnTimer */
#define  PANEL_2_RINGSLIDE_TYPEWIN        11      /* control type: slide, callback function: (none) */
#define  PANEL_2_NUM_FREQ                 12      /* control type: numeric, callback function: (none) */
#define  PANEL_2_NUM_POWER                13      /* control type: numeric, callback function: (none) */
#define  PANEL_2_RINGSLIDE_FILT           14      /* control type: slide, callback function: (none) */
#define  PANEL_2_GRAPH_7                  15      /* control type: graph, callback function: (none) */
#define  PANEL_2_DECORATION               16      /* control type: deco, callback function: (none) */
#define  PANEL_2_GRAPH_8                  17      /* control type: graph, callback function: (none) */
#define  PANEL_2_DECORATION_2             18      /* control type: deco, callback function: (none) */


     /* Control Arrays: */

          /* (no control arrays in the resource file) */


     /* Menu Bars, Menus, and Menu Items: */

#define  Menu                             1
#define  Menu_UNNAMEDM                    2


     /* Callback Prototypes: */

int  CVICALLBACK OnFiltrare(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnLoadButtonCB(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnMainPanel(int panel, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnNext(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnPrev(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnSecondPanel(int panel, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnSwitch(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);
int  CVICALLBACK OnTimer(int panel, int control, int event, void *callbackData, int eventData1, int eventData2);


#ifdef __cplusplus
    }
#endif