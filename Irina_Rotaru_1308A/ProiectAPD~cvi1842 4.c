#include <advanlys.h>
#include <ansi_c.h>
#include <cvirte.h>		
#include <userint.h>
#include <formatio.h>
#include "ProiectAPD.h"

static int panelHandle;
static int panelHandle2;

#define SAMPLE_RATE		0
#define NPOINTS			1

int waveInfo[2]; //waveInfo[0] = sampleRate
				 //waveInfo[1] = number of elements
double sampleRate = 0.0;
int npoints = 0;
double *waveData = 0;
double *anvelopa=0;
int *frecvente=0;
double *afisare=0;
double *semnalfil=0;
double freq=0.0;
double prev,next;

int main (int argc, char *argv[])
{
	if (InitCVIRTE (0, argv, 0) == 0)
		return -1;	/* out of memory */
	if ((panelHandle = LoadPanel (0, "ProiectAPD.uir", PANEL)) < 0)
		return -1;
	if ((panelHandle2 = LoadPanel (0, "ProiectAPD.uir", PANEL_2)) < 0)
		return -1;
	npoints = waveInfo[NPOINTS];
	DisplayPanel (panelHandle);
	RunUserInterface ();
	DiscardPanel (panelHandle);
	return 0;
}

int filtrare(double *semnal,double alpha, double *filtrat)
{
	for(int i=1;i<npoints;i++)
	{
		filtrat[i]=(1-alpha)*(filtrat[i-1])+alpha*semnal[i];
	}
	return 0;
}

void mediere(int n)
{
	double sum=0.0;
	for(int i=0;i<n-1;++i)
	{
		sum=sum+waveData[i];
	}
	for(int j=0;j<n-1;++j)
	{
		semnalfil[j]=sum/n;
	}
	for(int k=n;k<npoints;++k)
	{
		sum=sum-waveData[k-n]+waveData[k];
		semnalfil[k]=sum/n;
	}
	
}

int CVICALLBACK OnLoadButtonCB (int panel, int control, int event,
								void *callbackData, int eventData1, int eventData2)
{
	switch (event)
	{
		double min;
		double max;
		int minIndex;
		int maxIndex;
		double media;
		double dispersia;
		double mediana;
		int nrzero;
		int bitmapid;
		case EVENT_COMMIT:
			char FileName2[256]="SemnalNormal";
			FileToArray("waveInfo.txt", waveInfo, VAL_INTEGER, 2, 1, VAL_GROUPS_TOGETHER, VAL_GROUPS_AS_COLUMNS, VAL_ASCII);
			sampleRate = waveInfo[SAMPLE_RATE];
			waveData = (double *) calloc(npoints, sizeof(double));
			anvelopa = (double *) calloc(npoints, sizeof(double));
			frecvente=(ssize_t *) calloc(npoints,sizeof(ssize_t));
			afisare=(double *) calloc(npoints,sizeof(double));
			semnalfil=(double *)calloc(npoints,sizeof(double));
			FileToArray("waveData.txt", waveData, VAL_DOUBLE, npoints, 1, VAL_GROUPS_TOGETHER, VAL_GROUPS_AS_COLUMNS, VAL_ASCII);
			PlotY(panel, PANEL_GRAPH, waveData, npoints, VAL_DOUBLE, VAL_THIN_LINE, VAL_EMPTY_SQUARE, VAL_SOLID, VAL_CONNECTED_POINTS, VAL_RED);		
			MaxMin1D(waveData,npoints,&max,&maxIndex,&min,&minIndex);
			SetCtrlVal(panel,PANEL_NUMERIC,min);
			SetCtrlVal(panel,PANEL_NUMERIC_2,max);
			SetCtrlVal(panel,PANEL_Index_1,minIndex);
			SetCtrlVal(panel,PANEL_Index_2,maxIndex);
			Mean(waveData,npoints,&media);
			SetCtrlVal(panel,PANEL_NUMERIC_4,media);
			dispersia=(min-max)/(min+max);
			SetCtrlVal(panel,PANEL_NUMERIC_5,dispersia);
			Median(waveData,npoints,&mediana);
			SetCtrlVal(panel,PANEL_NUMERIC_6,mediana);
			nrzero=0;
			for(int i=0;i<npoints-1;i++)
			{
				if(waveData[i]*waveData[i+1]<0)
					nrzero=nrzero+1;
			}
			SetCtrlVal(panel,PANEL_NUMERIC_3,nrzero);
			Histogram(waveData,npoints,min-1,max+1,frecvente,afisare,40);
			SetAxisRange(panel,PANEL_GRAPH_2,VAL_AUTOSCALE,min-1,max+1,VAL_AUTOSCALE,0,max+1);
			DeleteGraphPlot(panel,PANEL_GRAPH_2,-1,VAL_DELAYED_DRAW);
			PlotXY(panel,PANEL_GRAPH_2,afisare,frecvente,40,VAL_DOUBLE,VAL_SSIZE_T,VAL_VERTICAL_BAR,VAL_SOLID_SQUARE,VAL_DOT, 1,VAL_RED);
			FileToArray("anvelopa.txt",anvelopa, VAL_DOUBLE, npoints, 1, VAL_GROUPS_TOGETHER, VAL_GROUPS_AS_COLUMNS, VAL_ASCII);
			PlotY(panel, PANEL_GRAPH, anvelopa, npoints, VAL_DOUBLE, VAL_THIN_LINE, VAL_EMPTY_SQUARE, VAL_SOLID, VAL_CONNECTED_POINTS, VAL_BLUE);	
			GetCtrlDisplayBitmap(panel,PANEL_GRAPH,1,&bitmapid);
			SaveBitmapToJPEGFile(bitmapid,FileName2,JPEG_PROGRESSIVE,100);
			break;
	}
	return 0;
}

//filt[i]=(1-alpha)*filt[i-1]+alpha*signal[i]

int CVICALLBACK OnMainPanel (int panel, int event, void *callbackData,
							 int eventData1, int eventData2)
{
	switch (event)
	{
		case EVENT_GOT_FOCUS:

			break;
		case EVENT_LOST_FOCUS:

			break;
		case EVENT_CLOSE:
			QuitUserInterface(0);
			break;
	}
	return 0;
}

int CVICALLBACK OnNext (int panel, int control, int event,
						void *callbackData, int eventData1, int eventData2)
{
	int bitmapid;
	double x=0.0;;
	char FileName[256]={0};
	switch (event)
	{
		case EVENT_COMMIT:
			if(next<133332)
			{
				GetCtrlVal(panel,PANEL_StartPoint,&x);
				sprintf(FileName,"SemnalFiltrat_sec_%1g.jpg",x);
				GetCtrlDisplayBitmap(panel,PANEL_GRAPH_3,1,&bitmapid);
				SaveBitmapToJPEGFile(bitmapid,FileName,JPEG_PROGRESSIVE,100);
				prev=prev+freq;
				next=next+freq;
				SetAxisScalingMode(panel,PANEL_GRAPH_3,VAL_BOTTOM_XAXIS,VAL_MANUAL,prev,next);
				SetCtrlVal(panel,PANEL_StartPoint,prev/freq);
				SetCtrlVal(panel,PANEL_StopPoint,next/freq);
				x++;				
			}
			else
			{
				GetCtrlVal(panel,PANEL_StartPoint,&x);
				sprintf(FileName,"SemnalFiltrat_sec_%1g.jpg",x);
				GetCtrlDisplayBitmap(panel,PANEL_GRAPH_3,1,&bitmapid);
				SaveBitmapToJPEGFile(bitmapid,FileName,JPEG_PROGRESSIVE,100);
			}
			
			break;
	}
	return 0;
}

int CVICALLBACK OnPrev (int panel, int control, int event,
						void *callbackData, int eventData1, int eventData2)
{
	switch (event)
	{
		case EVENT_COMMIT:
			if(prev>0)
			{
				prev=prev-freq;
				next=next-freq;
				SetAxisScalingMode(panel,PANEL_GRAPH_3,VAL_BOTTOM_XAXIS,VAL_MANUAL,prev,next);
				SetCtrlVal(panel,PANEL_StartPoint,prev/freq);
				SetCtrlVal(panel,PANEL_StopPoint,next/freq);
			}
			break;
	}
	return 0;
}

int CVICALLBACK OnFiltrare (int panel, int control, int event,
							void *callbackData, int eventData1, int eventData2)
{
	double alpha;
	int opt;
	switch (event)
	{
		case EVENT_COMMIT:
			GetCtrlVal(panel,PANEL_RINGSLIDE,&opt);
			if(opt==1)
			{
				GetCtrlVal(panel,PANEL_NUMERICSLIDE,&alpha);
				filtrare(waveData,alpha,semnalfil);
				PlotY(panel,PANEL_GRAPH_3,semnalfil,npoints,VAL_DOUBLE,VAL_THIN_LINE,VAL_EMPTY_SQUARE,VAL_SOLID,VAL_CONNECTED_POINTS,VAL_RED);
			}
			if(opt==2)
			{
				mediere(16);
				PlotY(panel,PANEL_GRAPH_3,semnalfil,npoints,VAL_DOUBLE,VAL_THIN_LINE,VAL_EMPTY_SQUARE,VAL_SOLID,VAL_CONNECTED_POINTS,VAL_RED);
			}
			prev=0;
			freq=npoints/6;
			next=freq;
			SetAxisScalingMode(panel,PANEL_GRAPH_3,VAL_BOTTOM_XAXIS,VAL_MANUAL,prev,next);
			break;
	}
	return 0;
}


int CVICALLBACK OnSwitch (int panel, int control, int event,
						  void *callbackData, int eventData1, int eventData2)
{
	switch (event)
	{
		case EVENT_COMMIT:
			if(panel == panelHandle)
			{
				SetCtrlVal(panelHandle2, PANEL_2_SwitchPanel, 1);
				DisplayPanel(panelHandle2);
				HidePanel(panel);
			}
			else
			{
				SetCtrlVal(panelHandle, PANEL_SwitchPanel, 0);
				DisplayPanel(panelHandle);
				HidePanel(panel);
			}
			break;
	}
	return 0;
}

int CVICALLBACK OnTimer (int panel, int control, int event,
						 void *callbackData, int eventData1, int eventData2)
{
	double window=0.0;
	WindowConst windconst;
	double dt=1.0/npoints;
	double autospectrum[npoints/2];
	double df=1.0;
	double freqpeak=0.0;
	double powerpeak=0.0;
	double sfreq=0.0;
	double freqspan=0.0;
	double spectrum[npoints];
	double sconverted[npoints];
	char unit[32]="V";
	switch (event)
	{
		case EVENT_TIMER_TICK:
			ScaledWindowEx(waveData,npoints,RECTANGLE,window,&windconst);
			AutoPowerSpectrum(waveData,npoints,dt,autospectrum,&df);
			PowerFrequencyEstimate(autospectrum,npoints/2,sfreq,windconst,df,freqspan,&freqpeak,&powerpeak);
			SpectrumUnitConversion(spectrum,npoints,SPECTRUM_POWER,SCALING_MODE_LINEAR,DISPLAY_UNIT_VRMS,df,windconst,sconverted,unit);
			PlotY(panelHandle2,PANEL_2_GRAPH,sconverted,npoints,VAL_DOUBLE,VAL_THIN_LINE,VAL_SOLID_SQUARE,VAL_SOLID,1,VAL_GREEN);
			break;
	}
	return 0;
}
