/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jlab.groot.fitter;

import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnScan;
import org.freehep.math.minuit.MnUserParameters;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.IDataSet;
import org.jlab.groot.math.F1D;
import org.jlab.groot.math.Func1D;
import org.jlab.groot.math.FunctionFactory;
import org.jlab.groot.math.UserParameter;


/**
 *
 * @author gavalian
 */
public class DataFitter {
    
    public DataFitter(){
        
    }
    
    public static void fit(Func1D func, IDataSet  data, String options){
        
        FitterFunction funcFitter = new FitterFunction(func,
                data,options);
        
        int npars = funcFitter.getFunction().getNPars();
        
        MnUserParameters upar = new MnUserParameters();
        for(int loop = 0; loop < npars; loop++){
            UserParameter par = funcFitter.getFunction().parameter(loop);
            upar.add(par.name(),par.value(),0.0001);
            if(par.min()>-1e9&&par.max()<1e9){
                upar.setLimits(par.name(), par.min(), par.max());
            }
        }
        
        
        MnScan  scanner = new MnScan(funcFitter,upar);
        FunctionMinimum scanmin = scanner.minimize(); 
        /*
        System.err.println("******************");
        System.err.println("*   SCAN RESULTS  *");
        System.err.println("******************");
        System.out.println("minimum : " + scanmin);
        System.out.println("pars    : " + upar);
        System.out.println(upar);
        System.err.println("*******************************************");
        */
        MnMigrad migrad = new MnMigrad(funcFitter, upar);
        FunctionMinimum min = migrad.minimize();
        
        MnUserParameters userpar = min.userParameters();
        /*
        for(int loop = 0; loop < npars; loop++){
            UserParameter par = funcFitter.getFunction().parameter(loop);
            par.setValue(userpar.value(par.name()));
            par.setError(userpar.error(par.name()));
        }*/
        
        /*
        System.out.println(upar);
        System.err.println("******************");
        System.err.println("*   FIT RESULTS  *");
        System.err.println("******************");

        System.err.println(min);
        */
    }
    
    public static void main(String[] args){
        H1F  h1 = FunctionFactory.randomGausian(80, 0.1, 0.8, 8000, 0.6, 0.1);
        H1F  h2 = FunctionFactory.randomGausian(80, 0.1, 0.8, 20000, 0.3, 0.05);
        F1D func = new F1D("f1","[amp]*gaus(x,[mean],[sigma])",0.1,0.8);
        func.setParameter(0, 10);
        func.setParameter(1, 0.4);
        func.setParameter(2, 0.2);
        DataFitter.fit(func, h1, "E");
        func.show();
    }
}
