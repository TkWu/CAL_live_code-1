package ci.function.Checkin;

public class CIAPISDef {

    /**特殊航線的國籍*/
    public static String NATIONAL_CHN = "CHN";
    public static String NATIONAL_TWN = "TWN";
    public static String NATIONAL_CAN = "CAN";
    public static String NATIONAL_HKG = "HKG";
    public static String NATIONAL_MAC = "MAC";
    public static String NATIONAL_USA = "USA";



    /**
     * 航線情境
     **/
    public enum CIRouteType {

        /**
         * 一般
         **/
        normal,
        /**
         * 抵達中國
         **/
        arrivalCHN,
        /**
         * 台灣出發抵達中國
         * （兩岸航線，中轉站只能是台灣或中國）
         **/
        arrivalCHNCrossstraitOnly,
        /**
         * 抵達中國，中轉台灣
         **/
        arrivalCHNTransitTWN,
        /**
         * 出發中國，中轉台灣
         **/
        departureCHNTransitTWN,
        /**
         * 出發中國，抵達台灣
         **/
        departureCHNArrivalTWN,
        /**
         * 抵達香港
         **/
        arrivalHKG,
        /**
         * 抵達香港，經台灣中轉
         **/
        arrivalHKGTransitTWN,
        /**
         * 出發香港，中轉台灣
         **/
        departureHKGTransitTWN,
        /**
         * 出發香港，抵達台灣
         **/
        departureHKGArrivalTWN,
        /**
         * 抵達美國
         **/
        arrivalUSA,
        /**
         * 抵達加拿大
         **/
        arrivalCAN
    }


    /**
     * 不能呼叫remark 的場站
     * * 2017-08-28 Modifly by Ryan , 更新
     */
    private static final String[] IS_NON_REMARKSTATION = new String[]{
            //澳洲
            "SYD", "BNE", "MEL",
            //紐西蘭
            "AKL", "CHC",
            //關島
            "GUM",
            //帛琉
            "ROR",
            //美國站
            "HNL","SFO","LAX","ONT","JFK"
    };

    /**
     * 美國線的航站資料
     * * 2017-08-28 Modifly by Ryan , 更新
     */
    //String[] strUSAStationList = new String[]{"LAX","SFO","JFK","HNL","ANC","GUM","SEA","IAH"};
    private static final String[] USA_STATION_LIST = new String[]{
            "AAF","ABE","ABI","ABL","ABQ","ABR","ABY","ACB","ACK","ACT","ACV","ACY","ADG","ADK","ADQ","ADS","ADW","AEL","AEX","AFF","AFN","AFO","AFW","AGC","AGO","AGS","AHC","AHH","AHN","AIA","AID","AIK","AIO","AIV","AIZ","AKB","AKI","AKK","AKN","AKO","AKP","ALB","ALI","ALM","ALN","ALO","ALS","ALW","ALX","AMA","AMN","AMW","ANB","ANC","AND","ANI","ANN","ANP","ANQ","ANV","ANW","ANY","AOH","AOO","APC","APF","APG","APH","APN","APT","APV","ARB","ARC","ARG","ART","ARV","ASE","ASH","ASL","ASN","ASX","ASY","ATK","ATL","ATS","ATY","AUG","AUK","AUM","AUN","AUO","AUS","AUW","AUZ","AVL","AVO","AVP","AVX","AWM","AXN","AXS","AXV","AZO","BAB","BAD","BAF","BBD","BBW","BCB","BCE","BCT","BDE","BDG","BDL","BDR","BEC","BED","BEH","BET","BFD","BFF","BFI","BFL","BFM","BFR","BFT","BGD","BGE","BGM","BGQ","BGR","BHB","BHM","BID","BIE","BIF","BIH","BIL","BIS","BIX","BJC","BJI","BJJ","BKC","BKD","BKE","BKH","BKL","BKT","BKW","BKX","BLF","BLH","BLI","BLM","BLU","BLV","BMC","BMG","BMI","BML","BMT","BNA","BNG","BNL","BNO","BNW","BOI","BOK","BOS","BOW","BPI","BPT","BQK","BRD","BRL","BRO","BRW","BRY","BSF","BTF","BTI","BTL","BTM","BTP","BTR","BTT","BTV","BTY","BUB","BUF","BUM","BUR","BVO","BVU","BVX","BVY","BWC","BWD","BWG","BWI","BXA","BXK","BYG","BYH","BYI","BZN","CAD","CAE","CAK","CAO","CAR","CBE","CBF","CBK","CBM","CCB","CCR","CCY","CDB","CDC","CDH","CDK","CDN","CDR","CDS","CDV","CDW","CEA","CEC","CEF","CEM","CEU","CEV","CEW","CEY","CEZ","CFD","CFT","CFV","CGE","CGF","CGI","CGZ","CHA","CHK","CHO","CHS","CIC","CID","CIK","CIN","CIR","CIU","CKA","CKB","CKM","CKN","CKV","CLD","CLE","CLI","CLK","CLL","CLM","CLP","CLR","CLS","CLT","CLU","CLW","CMH","CMI","CMX","CMY","CNH","CNK","CNM","CNO","CNU","CNW","CNY","COD","COE","COF","COI","COM","CON","COS","COT","COU","CPM","CPR","CPS","CRE","CRG","CRP","CRS","CRT","CRW","CRX","CSG","CSM","CSQ","CSV","CTB","CTY","CTZ","CUB","CUH","CVG","CVN","CVO","CVS","CWA","CWF","CWI","CXF","CXL","CXO","CYF","CYS","CZF","CZK","CZT","DAA","DAB","DAG","DAL","DAN","DAY","DBN","DBQ","DCA","DCU","DDC","DEC","DEH","DEN","DET","DFI","DFW","DGL","DGW","DHN","DHT","DIK","DJN","DKK","DLF","DLG","DLH","DLL","DLN","DLS","DMA","DMN","DMO","DNL","DNN","DNS","DNV","DOV","DPA","DPG","DRG","DRI","DRO","DRT","DSM","DSV","DTA","DTL","DTN","DTW","DUA","DUG","DUJ","DUT","DVL","DVN","DVT","DWH","DYS","EAA","EAN","EAR","EAT","EAU","EBS","ECG","ECP","ECS","EDE","EDF","EDG","EDW","EED","EEK","EEN","EFD","EFK","EFW","EGE","EGI","EGV","EGX","EHM","EHO","EIL","EKA","EKN","EKO","EKX","ELA","ELD","ELI","ELK","ELM","ELN","ELP","ELY","ELZ","EMK","EMM","EMP","EMT","ENA","END","ENL","ENN","ENV","ENW","EOK","EOS","EPH","ERI","ERV","ESF","ESN","EST","ESW","ETB","EUF","EUG","EVM","EVV","EVW","EWB","EWK","EWN","EWR","EYW","FAF","FAI","FAM","FAR","FAT","FAY","FBG","FBK","FBL","FBY","FCA","FCH","FCM","FCS","FCY","FDK","FDR","FDY","FEP","FET","FFA","FFM","FFO","FFT","FHU","FKL","FKN","FLD","FLG","FLL","FLO","FLP","FLV","FLX","FME","FMH","FMN","FMY","FNL","FNT","FOD","FOE","FOK","FPR","FRH","FRI","FRM","FRN","FRR","FSD","FSI","FSK","FSM","FST","FSU","FTK","FTW","FTY","FUL","FWA","FXE","FXY","FYM","FYU","FYV","GAB","GAD","GAG","GAI","GAL","GAM","GBD","GBG","GBH","GBR","GCC","GCK","GCN","GCY","GDM","GDV","GED","GEG","GEY","GFD","GFK","GFL","GGE","GGG","GGW","GHM","GIF","GJT","GKN","GKT","GLD","GLE","GLH","GLR","GLS","GLV","GLW","GMU","GNG","GNT","GNV","GOK","GON","GPT","GPZ","GQQ","GRB","GRD","GRE","GRF","GRI","GRK","GRN","GRR","GSB","GSH","GSO","GSP","GST","GTF","GTR","GUC","GUP","GUS","GUY","GVE","GVL","GVT","GWO","GWS","GXY","GYR","GYY","HAB","HAF","HAI","HAO","HBG","HBR","HDE","HDH","HDN","HEE","HES","HEZ","HFD","HFF","HGR","HHI","HHR","HIB","HIE","HIF","HII","HIO","HKA","HKS","HKY","HLB","HLC","HLG","HLM","HLN","HLR","HMN","HMT","HNB","HNH","HNL","HNM","HNS","HOB","HOM","HON","HOP","HOT","HOU","HPB","HPN","HPT","HPY","HQM","HRL","HRO","HSB","HSI","HSL","HSP","HST","HSV","HTH","HTL","HTO","HTS","HTW","HUA","HUF","HUL","HUM","HUS","HUT","HVE","HVN","HVR","HWD","HWO","HYA","HYR","HYS","HZL","IAB","IAD","IAG","IAH","IAN","ICL","ICT","IDA","IDG","IDI","IDP","IFA","IFP","IGG","IGM","IJX","IKK","IKO","ILE","ILG","ILI","ILM","ILN","IML","IMM","IMT","IND","INK","INL","INS","INT","INW","IOW","IPL","IPT","IRC","IRK","IRS","ISM","ISN","ISO","ISP","ISQ","ISW","ITO","IWD","IWS","IYK","JAC","JAN","JAS","JAX","JBR","JCT","JDN","JEF","JFK","JHM","JHW","JLN","JMS","JNU","JOT","JRF","JST","JVL","JXN","KAE","KAL","KCQ","KDK","KFP","KGK","KGX","KIC","KKA","KKH","KLG","KLS","KLW","KMO","KNB","KNW","KOA","KPC","KPV","KSM","KTN","KTS","KVC","KVL","KWK","KWN","KWT","KYK","KYU","LAA","LAF","LAL","LAM","LAN","LAR","LAS","LAW","LAX","LBB","LBE","LBF","LBL","LBT","LCH","LCI","LCK","LCQ","LDJ","LDM","LEB","LEE","LEM","LEW","LEX","LFI","LFK","LFT","LGA","LGB","LGC","LGD","LGF","LGU","LHV","LIC","LIH","LIT","LKK","LKP","LKV","LMS","LMT","LNA","LND","LNK","LNN","LNP","LNR","LNS","LNY","LOL","LOT","LOU","LOZ","LPC","LQK","LRD","LRF","LRJ","LRU","LSB","LSE","LSF","LSK","LSN","LSV","LTS","LUF","LUK","LUL","LUP","LUR","LVK","LVL","LVM","LVS","LWB","LWL","LWM","LWS","LWT","LWV","LXN","LXV","LYH","LYO","LYU","LZU","MAE","MAF","MAW","MBG","MBL","MBS","MBY","MCB","MCC","MCD","MCE","MCF","MCG","MCI","MCK","MCL","MCN","MCO","MCW","MDA","MDD","MDF","MDH","MDO","MDT","MDW","MEI","MEM","MER","MEV","MFD","MFE","MFI","MFR","MFV","MGC","MGE","MGJ","MGM","MGR","MGW","MGY","MHE","MHK","MHL","MHN","MHR","MHT","MHV","MIA","MIB","MIC","MIE","MIV","MIW","MJQ","MJX","MKC","MKE","MKG","MKK","MKL","MKO","MKT","MLB","MLC","MLD","MLF","MLI","MLJ","MLL","MLS","MLT","MLU","MLY","MMH","MMI","MML","MMS","MMT","MMU","MNM","MNN","MOB","MOD","MOP","MOR","MOT","MOU","MPJ","MPO","MPR","MPV","MPZ","MQB","MQT","MQW","MQY","MRB","MRC","MRF","MRI","MRN","MRY","MSL","MSN","MSO","MSP","MSS","MSV","MSY","MTC","MTH","MTJ","MTN","MTO","MTP","MTW","MUE","MUI","MUL","MUO","MUT","MVE","MVL","MVM","MVN","MVY","MWA","MWC","MWH","MWL","MWM","MWO","MXA","MXF","MXO","MYF","MYL","MYR","MYU","MYV","MZJ","MZZ","NBG","NEL","NEN","NEW","NFL","NGF","NGP","NGU","NHK","NIB","NIP","NJK","NKX","NLC","NLG","NPA","NQA","NQI","NQX","NRB","NRS","NSE","NTD","NTU","NUI","NUL","NUQ","NUW","NVD","NYG","NZY","OAJ","OAK","OAR","OBE","OBU","OCF","OCH","OCW","OEO","OFF","OFK","OGA","OGB","OGD","OGG","OGS","OIC","OJC","OKC","OKK","OKM","OKS","OLD","OLE","OLF","OLM","OLS","OLU","OLV","OLY","OMA","OME","OMK","ONA","ONL","ONM","ONO","ONP","ONT","ONY","OOA","OOK","OPF","OPL","ORD","ORF","ORH","ORL","ORT","OSC","OSH","OSU","OSX","OTG","OTH","OTM","OTZ","OUN","OVE","OWB","OWD","OWK","OXC","OXD","OZA","OZR","PAE","PAH","PAK","PAM","PAQ","PBF","PBG","PBI","PDK","PDT","PDX","PEQ","PFC","PGA","PGD","PGR","PGV","PHD","PHF","PHK","PHL","PHN","PHO","PHP","PHT","PHX","PIA","PIB","PIE","PIH","PIM","PIP","PIR","PIT","PIZ","PKA","PKB","PKD","PKF","PLK","PLN","PLR","PMB","PMD","PMH","PNC","PNE","PNN","PNS","POB","POC","POE","POF","POH","POU","POY","PPA","PPC","PPF","PQI","PRB","PRC","PRO","PRX","PSB","PSC","PSF","PSG","PSK","PSM","PSN","PSP","PSX","PTB","PTH","PTK","PTN","PTS","PTT","PTU","PTV","PTW","PUB","PUC","PUW","PVC","PVD","PVF","PVU","PVW","PWA","PWD","PWK","PWM","PWT","PYM","RAC","RAL","RAP","RBD","RBG","RBL","RBW","RBY","RCA","RCK","RCR","RCT","RDB","RDD","RDG","RDM","RDR","RDU","REO","RFD","RFG","RHI","RHV","RIC","RID","RIF","RIL","RIR","RIV","RIW","RKD","RKP","RKR","RKS","RKW","RLD","RME","RMG","RNC","RND","RNH","RNO","RNT","ROA","ROC","ROG","ROW","ROX","RPX","RRL","RRT","RSH","RSL","RSN","RST","RSW","RTN","RUI","RUT","RVS","RWF","RWI","RWL","RXE","SAA","SAC","SAD","SAF","SAN","SAR","SAT","SAV","SBA","SBD","SBM","SBN","SBP","SBS","SBX","SBY","SCB","SCC","SCH","SCK","SCM","SDF","SDM","SDP","SDY","SEA","SEE","SEF","SEG","SEM","SEP","SER","SFB","SFF","SFM","SFO","SFZ","SGF","SGH","SGT","SGY","SHD","SHG","SHH","SHN","SHR","SHV","SHX","SIK","SIT","SIV","SIY","SJC","SJN","SJT","SKA","SKF","SKW","SLB","SLC","SLE","SLG","SLK","SLN","SLO","SLQ","SLR","SMD","SME","SMF","SMK","SMN","SMO","SMU","SMX","SNA","SNK","SNL","SNP","SNS","SNY","SOP","SOV","SOW","SPA","SPF","SPG","SPI","SPS","SPZ","SQA","SQI","SQL","SRC","SRQ","SSC","SSF","SSI","STC","STE","STG","STJ","STK","STL","STP","STT","STX","SUA","SUE","SUN","SUS","SUU","SUW","SUX","SVA","SVC","SVE","SVH","SVN","SVW","SWD","SWF","SWO","SWW","SXQ","SYA","SYI","SYN","SYR","SYV","SZL","SZP","TAD","TAL","TBN","TBR","TCC","TCL","TCM","TCS","TCT","TDO","TDW","TDZ","TEB","TEX","THA","THM","THP","THV","TIK","TIW","TIX","TKA","TLA","TLH","TLJ","TLR","TMA","TMB","TNC","TNP","TNT","TNU","TOA","TOC","TOG","TOI","TOL","TOP","TOR","TPA","TPF","TPH","TPL","TRI","TRL","TRM","TRX","TSP","TTD","TTN","TUL","TUP","TUS","TVC","TVF","TVI","TVL","TWF","TXK","TYR","TYS","UBS","UCY","UDD","UES","UGN","UIL","UIN","UKI","UKT","ULM","UMM","UMT","UNK","UNU","UOS","UOX","UPP","UTO","UUK","UVA","VAD","VAK","VBG","VCT","VCV","VDI","VDZ","VEE","VEL","VGT","VHN","VIH","VIS","VJI","VKS","VLA","VLD","VNY","VOK","VPZ","VQQ","VRB","VSF","VTN","VYS","WAA","WAY","WBQ","WBW","WCR","WDG","WDR","WEA","WFK","WHP","WJF","WLK","WLW","WMC","WMO","WNA","WOW","WRB","WRG","WRI","WRL","WSN","WST","WTK","WVI","WVL","WWA","WWD","WWR","WWT","WYS","XNA","YAK","YIP","YKM","YKN","YNG","ZPH","ZZV"
    };


    /**
     * 大陸航站
     * * 2017-08-28 Modifly by Ryan , 更新
     */
    private static final String[] CN_STATION_LIST = new String[]{
            "CAN","CGO","CGQ","CKG","CSX","CTU","CZX","DLC","DNH","DYG","FOC","HAK","HET","HFE","HGH","HLD","HRB","HTN","INC","JGN","JHG","JJN","JMU","KCA","KHG","KHN","KMG","KWE","KWL","LHW","LJG","LXA","MDG","NDG","NGB","NKG","NNG","NTG","PEK","PVG","SHA","SHE","SJW","SWA","SYX","SZX","TAO","TNA","TSN","TXN","TYN","URC","WEH","WNZ","WUH","WUX","XIC","XIY","XMN","XNN","XUZ","YIW","YNJ","YNT","YNZ"
    };

    /**
     * 香港航站
     */
    private static final String[] HK_STATION_LIST = new String[]{"HKG"};

    /**
     * 加拿大航站
     * * 2017-08-28 Modifly by Ryan , 更新
     */
    private static final String[] CAN_STATION_LIST = new String[]{
            "AKV","ILF","KES","LAK","MSA","PIW","QBC","WPC","XGR","XKS","XLB","XPK","XPP","XRR","XSI","XTL","YAC","YAG","YAL","YAM","YAT","YAW","YAY","YAZ","YBB","YBC","YBE","YBG","YBK","YBL","YBR","YBT","YBV","YBX","YBY","YCB","YCC","YCD","YCE","YCG","YCH","YCL","YCM","YCN","YCO","YCQ","YCR","YCS","YCT","YCW","YCY","YCZ","YDA","YDB","YDF","YDG","YDL","YDN","YDO","YDP","YDQ","YDT","YDV","YED","YEG","YEK","YEL","YEM","YEN","YER","YET","YEU","YEV","YEY","YFA","YFB","YFC","YFE","YFH","YFO","YFR","YFS","YGB","YGH","YGK","YGL","YGM","YGO","YGP","YGQ","YGR","YGT","YGV","YGW","YGX","YGZ","YHB","YHD","YHE","YHF","YHI","YHK","YHM","YHN","YHO","YHR","YHT","YHU","YHY","YHZ","YIB","YIF","YIK","YIO","YIV","YJF","YJN","YJT","YKA","YKC","YKF","YKG","YKJ","YKL","YKQ","YKX","YKY","YKZ","YLB","YLC","YLD","YLH","YLJ","YLL","YLQ","YLR","YLT","YLW","YLY","YMA","YME","YMG","YMH","YMJ","YML","YMM","YMN","YMO","YMT","YMW","YMX","YNA","YNC","YND","YNE","YNH","YNL","YNM","YNS","YOA","YOC","YOD","YOH","YOJ","YOO","YOP","YOS","YOW","YPA","YPC","YPE","YPG","YPH","YPJ","YPL","YPM","YPN","YPO","YPQ","YPR","YPS","YPT","YPW","YPX","YPY","YPZ","YQA","YQB","YQC","YQD","YQF","YQG","YQH","YQI","YQK","YQL","YQM","YQN","YQQ","YQR","YQS","YQT","YQU","YQV","YQW","YQX","YQY","YQZ","YRB","YRF","YRI","YRJ","YRL","YRM","YRO","YRQ","YRS","YRT","YRV","YSB","YSC","YSE","YSF","YSH","YSJ","YSK","YSL","YSM","YSN","YSP","YST","YSU","YSY","YTA","YTD","YTE","YTF","YTH","YTL","YTM","YTQ","YTR","YTS","YTZ","YUB","YUD","YUL","YUT","YUX","YUY","YVB","YVC","YVE","YVG","YVM","YVO","YVP","YVQ","YVR","YVT","YVV","YVZ","YWB","YWG","YWJ","YWK","YWL","YWP","YWY","YXC","YXE","YXH","YXJ","YXK","YXL","YXN","YXP","YXQ","YXR","YXS","YXT","YXU","YXX","YXY","YXZ","YYB","YYC","YYD","YYE","YYF","YYG","YYH","YYJ","YYL","YYM","YYN","YYQ","YYR","YYT","YYU","YYW","YYY","YYZ","YZE","YZF","YZG","YZH","YZP","YZR","YZS","YZT","YZU","YZV","YZW","YZX","ZAC","ZBF","ZBM","ZEL","ZEM","ZFA","ZFD","ZFM","ZFN","ZGF","ZGI","ZGR","ZHP","ZJG","ZJN","ZKE","ZMH","ZMT","ZPB","ZPO","ZRJ","ZSJ","ZST","ZTM","ZUC","ZUM","ZWL"
    };


    /**
     * 台灣航站
     * * 2017-08-28 Modifly by Ryan , 更新
     */
    private static final String[] TWN_STATION_LIST = new String[]{
            "CYI","GNI","HCN","HSZ","HUN","KHH","KNH","KYD","MZG","RMQ","TNN","TPE","TSA","TTT"
    };


    /**
     * 檢查該航站是否為台灣航站
     */
    public static boolean bIsTWNStation(String strStation) {

        for (String strUSAStation : TWN_STATION_LIST) {
            if (strUSAStation.equals(strStation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 檢查該航站是否為美國站
     */
    public static boolean bIsUSAStation(String strStation) {

        for (String strUSAStation : USA_STATION_LIST) {
            if (strUSAStation.equals(strStation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 檢查該航站是否為中國站
     */
    public static boolean bIsCNStation(String strStation) {

        for (String strUSAStation : CN_STATION_LIST) {
            if (strUSAStation.equals(strStation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 檢查該航站是否為香港航站
     */
    public static boolean bIsHKGStation(String strStation) {

        for (String strUSAStation : HK_STATION_LIST) {
            if (strUSAStation.equals(strStation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 檢查該航站是否為加拿大航站
     */
    public static boolean bIsCANStation(String strStation) {

        for (String strUSAStation : CAN_STATION_LIST) {
            if (strUSAStation.equals(strStation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 檢查該航站是否為不能呼叫remark 的場站
     */
    public static boolean bIsNonReMarkStation(String strStation) {

        for (String strUSAStation : IS_NON_REMARKSTATION) {
            if (strUSAStation.equals(strStation)) {
                return true;
            }
        }
        return false;
    }
}
