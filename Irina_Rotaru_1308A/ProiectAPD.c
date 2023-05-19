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
			npoints=waveInfo[NPOINTS];
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
			SetCtrlAttribute(panelHandle2,PANEL_2_TIMER,ATTR_ENABLED,1);
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
				mediere(32);
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
	int nrpoints=waveInfo[NPOINTS];
	WindowConst windconst;
	double dt=0.0;
	double *autospectrum;
	double df=0.0;
	double freqpeak=0.0;
	double powerpeak=0.0;
	double *spectrum;
	double *sconverted;
	char unit[32]="V";
	double *filtredsignal;
	int nr;
	char nume[100]={0};
	char nume2[100]={0};
	switch (event)
	{
		case EVENT_TIMER_TICK:
			spectrum=(double *)calloc(nrpoints/2,sizeof(double));
			sconverted=(double *)calloc(nrpoints,sizeof(double));
			autospectrum=(double *)calloc(nrpoints,sizeof(double));
			GetCtrlVal(panelHandle2,PANEL_2_RING_NumPoint,&nr);
			dt=1.0/nr;
			ScaledWindowEx(waveData,nr,RECTANGLE,window,&windconst);
			AutoPowerSpectrum(waveData,nr/2,dt,autospectrum,&df);
			PowerFrequencyEstimate(autospectrum,nr/2,1.0,windconst,df,7,&freqpeak,&powerpeak);
			SpectrumUnitConversion(autospectrum,nr/2,SPECTRUM_POWER,SCALING_MODE_LINEAR,DISPLAY_UNIT_VRMS,df,windconst,sconverted,unit);
			DeleteGraphPlot(panelHandle2,PANEL_2_GRAPH_2,-1,VAL_IMMEDIATE_DRAW);
			PlotWaveform(panelHandle2,PANEL_2_GRAPH_2,sconverted,nr/2,VAL_DOUBLE,1,0,0,df,VAL_THIN_LINE,VAL_EMPTY_SQUARE,VAL_SOLID,VAL_CONNECTED_POINTS,VAL_GREEN);
			DeleteGraphPlot(panelHandle2,PANEL_2_GRAPH,-1,VAL_IMMEDIATE_DRAW);
			PlotY(panelHandle2,PANEL_2_GRAPH,waveData,nr,VAL_DOUBLE,VAL_THIN_LINE,VAL_EMPTY_SQUARE,VAL_SOLID,VAL_CONNECTED_POINTS,VAL_RED);
			SetCtrlVal(panelHandle2,PANEL_2_NUM_FREQ,freqpeak);
			SetCtrlVal(panelHandle2,PANEL_2_NUM_POWER,powerpeak);
			double *fereastra;
			fereastra=(double *)calloc(nr,sizeof(double));
			int tip=0;
			GetCtrlVal(panelHandle2,PANEL_2_RINGSLIDE_TYPEWIN,&tip);
			LinEv1D(fereastra,nr,0,1,fereastra);
			if(tip==1)
			{
				HanWin(fereastra,nr);
				strcat(nume,"Hanning_");
			}
			if(tip==2)
			{
				BkmanWin(fereastra,nr);
				strcat(nume,"Blackman_");
			}
			DeleteGraphPlot(panelHandle2,PANEL_2_GRAPH_3,-1,VAL_IMMEDIATE_DRAW);
			PlotY(panelHandle2,PANEL_2_GRAPH_3,fereastra,nr,VAL_DOUBLE,VAL_THIN_LINE,VAL_EMPTY_SQUARE,VAL_SOLID,VAL_CONNECTED_POINTS,VAL_BLUE);
			double *win;
			win=(double *)calloc(nr,sizeof(double));
			Mul1D(waveData,fereastra,nr,win);
			DeleteGraphPlot(panelHandle2,PANEL_2_GRAPH_8,-1,VAL_IMMEDIATE_DRAW);
			PlotY(panelHandle2,PANEL_2_GRAPH_8,win,nr,VAL_DOUBLE,VAL_THIN_LINE,VAL_EMPTY_SQUARE,VAL_SOLID,VAL_CONNECTED_POINTS,VAL_MAGENTA);
			int fil;
			filtredsignal=(double *)calloc(2*nrpoints,sizeof(double));
			GetCtrlVal(panelHandle2,PANEL_2_RINGSLIDE_FILT,&fil);
			double *coef;
			coef=(double *)calloc(nr,sizeof(double));
			sampleRate = waveInfo[SAMPLE_RATE];
			if(fil==1)
			{
				Ksr_LPF(sampleRate,1000,55,coef,4.5);
				Convolve(coef,55,waveData,nr,filtredsignal);
				strcat(nume,"Ksr_LPF_");
			}
			if(fil==2)
			{
				Bw_LPF(waveData,nr,sampleRate,1000,6,filtredsignal);
				strcat(nume,"Butterworth_");
			}
			DeleteGraphPlot(panelHandle2,PANEL_2_GRAPH_6,-1,VAL_IMMEDIATE_DRAW);
			PlotY(panelHandle2,PANEL_2_GRAPH_6,filtredsignal,nr,VAL_DOUBLE,VAL_THIN_LINE, VAL_NO_POINT, VAL_SOLID, 1, VAL_RED);
			double *autospectru3;
			double *convertedspectru3;
			double ffpeak,pppeak;
			autospectru3=(double *)calloc(nr/2,sizeof(double));
			convertedspectru3=(double *)calloc(nr/2,sizeof(double));
			ScaledWindowEx(filtredsignal,nr,RECTANGLE,0,&windconst);
			AutoPowerSpectrum(filtredsignal,nr/2,dt,autospectru3,&df);
			PowerFrequencyEstimate(autospectru3,nr/2,1,windconst,df,7,&ffpeak,&pppeak);
			SpectrumUnitConversion(autospectru3,nr/2,SPECTRUM_POWER,SCALING_MODE_LINEAR,DISPLAY_UNIT_VRMS,df,windconst,convertedspectru3,unit);
			DeleteGraphPlot(panelHandle2,PANEL_2_GRAPH_7,-1,VAL_IMMEDIATE_DRAW);
			PlotWaveform(panelHandle2,PANEL_2_GRAPH_7,convertedspectru3,nr/2,VAL_DOUBLE,1,0,0,df,VAL_THIN_LINE,
						 VAL_EMPTY_SQUARE,VAL_SOLID,VAL_CONNECTED_POINTS,VAL_DK_YELLOW);
			double *winfil;
			winfil=(double *)calloc(nr,sizeof(double));
			Mul1D(filtredsignal,fereastra,nr,winfil);
			DeleteGraphPlot(panelHandle2,PANEL_2_GRAPH_4,-1,VAL_IMMEDIATE_DRAW);
			PlotY(panelHandle2,PANEL_2_GRAPH_4,winfil,nr,VAL_DOUBLE,VAL_THIN_LINE,VAL_EMPTY_SQUARE,VAL_SOLID,VAL_CONNECTED_POINTS,VAL_MAGENTA);
			double *autospectru4;
			double *convertedspectru4;
			autospectru4=(double *)calloc(nr/2,sizeof(double));
			convertedspectru4=(double *)calloc(nr/2,sizeof(double));
			ScaledWindowEx(winfil,nr,RECTANGLE,0,&windconst);
			AutoPowerSpectrum(winfil,nr/2,dt,autospectru4,&df);
			PowerFrequencyEstimate(autospectru4,nr/2,1,windconst,df,7,&ffpeak,&pppeak);
			SpectrumUnitConversion(autospectru4,nr/2,SPECTRUM_POWER,SCALING_MODE_LINEAR,DISPLAY_UNIT_VRMS,df,windconst,convertedspectru4,unit);
			DeleteGraphPlot(panelHandle2,PANEL_2_GRAPH_5,-1,VAL_IMMEDIATE_DRAW);
			PlotWaveform(panelHandle2,PANEL_2_GRAPH_5,convertedspectru4,nr/2,VAL_DOUBLE,1,0,0,df,VAL_THIN_LINE,
						 VAL_EMPTY_SQUARE,VAL_SOLID,VAL_CONNECTED_POINTS,VAL_CYAN);
			int imghandle;
			double sec=(double)nr*6/nrpoints;
			char interval[50]={0};
			char interval2[50]={0};
			char nume3[100]={0};
			char nume4[100]={0};
			char interval3[50]={0};
			char interval4[50]={0};
			strcat(nume2,nume);
			strcat(nume3,nume);
			strcat(nume4,nume);
			sprintf(interval,"int0_%f.jpg",sec);
			strcat(nume,interval);
			GetCtrlDisplayBitmap(panelHandle2,PANEL_2_GRAPH,1,&imghandle);
			SaveBitmapToJPEGFile(imghandle,nume,JPEG_PROGRESSIVE,100);
			DiscardBitmap(imghandle);
			GetCtrlDisplayBitmap(panelHandle2,PANEL_2_GRAPH_2,1,&imghandle);
			sprintf(interval2,"int0_%f_spectru.jpg",sec);
			strcat(nume2,interval2);
			SaveBitmapToJPEGFile(imghandle,nume2,JPEG_PROGRESSIVE,100);
			DiscardBitmap(imghandle);
			sprintf(interval3,"filtrat_int0_%f.jpg",sec);
			strcat(nume3,interval3);
			GetCtrlDisplayBitmap(panelHandle2,PANEL_2_GRAPH_6,1,&imghandle);
			SaveBitmapToJPEGFile(imghandle,nume3,JPEG_PROGRESSIVE,100);
			DiscardBitmap(imghandle);
			sprintf(interval4,"filtrat_spectru_int0_%f.jpg",sec);
			strcat(nume4,interval4);
			GetCtrlDisplayBitmap(panelHandle2,PANEL_2_GRAPH_5,1,&imghandle);
			SaveBitmapToJPEGFile(imghandle,nume4,JPEG_PROGRESSIVE,100);
			DiscardBitmap(imghandle);
			break;
	}
	//Ksr_LPF(freq,1000,npoints,filtredsignal,beta);
	//Bw_LPF(waveData,nrpoints,freq,1250,1,filtredsignal);
	//HANNING
	//BLKMAN
	//FIR Ksr_LPF si Butterworth trece jos de grad 6 fcut=1000 si fpass=1250 Hz
	return 0;
}


int CVICALLBACK OnSecondPanel (int panel, int event, void *callbackData,
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

