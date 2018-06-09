package jVVO;

import java.util.Optional;

public class GaussKrueger {

    /*
     Copyright (c) 2006, HELMUT H. HEIMEIER
     Permission is hereby granted, free of charge, to any person obtaining a
     copy of this software and associated documentation files (the "Software"),
     to deal in the Software without restriction, including without limitation
     the rights to use, copy, modify, merge, publish, distribute, sublicense,
     and/or sell copies of the Software, and to permit persons to whom the
     Software is furnished to do so, subject to the following conditions:
     The above copyright notice and this permission notice shall be included
     in all copies or substantial portions of the Software.
     */

    public static Optional<WGSCoordinate> gk2wgs(GKCoordinate gk) {
        Optional<GKCoordinate> pot = gk2pot(gk);
        if (!pot.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(pot2wgs(pot.get()));
    }

    public static Optional<GKCoordinate> wgs2gk(WGSCoordinate wgs)
    {
        Optional<GKCoordinate> pot = wgs2pot(wgs);
        if (!pot.isPresent()) {
            return Optional.empty();
        }
        return pot2gk(pot.get());
    }
    /// Die Funktion wandelt GK Koordinaten in geographische Koordinaten
    /// um. Rechtswert rw und Hochwert hw müssen gegeben sein.
    /// Berechnet werden geographische Länge lp und Breite bp
    /// im Potsdam Datum.
    public static Optional<GKCoordinate> gk2pot(GKCoordinate coordinate) {
        final Double rw = coordinate.getX();
        final Double hw = coordinate.getY();

        // Potsdam Datum
        // Große Halbachse a und Abplattung f
        final Double a = 6377397.155;
        final Double f = 3.342773154e-3;
        final Double pi = Math.PI;

        // Polkrümmungshalbmesser c
        final Double c = a/(1-f);

        // Quadrat der zweiten numerischen Exzentrizität
        final Double ex2 = (2*f-f*f)/((1-f)*(1-f));
        final Double ex4 = ex2*ex2;
        final Double ex6 = ex4*ex2;
        final Double ex8 = ex4*ex4;

        // Koeffizienten zur Berechnung der geographischen Breite aus gegebener Meridianbogenlänge
        final Double e0 = c*(pi/180)*(1 - 3*ex2/4 + 45*ex4/64  - 175*ex6/256  + 11025*ex8/16384);
        final Double f2 =   (180/pi)*(    3*ex2/8 -  3*ex4/16  + 213*ex6/2048 -   255*ex8/4096);
        final Double f4 =              (180/pi)*(   21*ex4/256 -  21*ex6/256  +   533*ex8/8192);
        final Double f6 =                           (180/pi)*(   151*ex6/6144 -   453*ex8/12288);

        // Geographische Breite bf zur Meridianbogenlänge gh = hw
        final Double sigma = hw/e0;
        final Double sigmr = sigma*pi/180;
        final Double bf = sigma + f2*Math.sin(2*sigmr) + f4*Math.sin(4*sigmr) + f6*Math.sin(6*sigmr);

        // Breite bf in Radianten
        final Double br = bf * pi/180;
        final Double tan1 = Math.tan(br);
        final Double tan2 = tan1*tan1;
        final Double tan4 = tan2*tan2;

        final Double cos1 = Math.cos(br);
        final Double cos2 = cos1*cos1;

        final Double etasq = ex2*cos2;

        // Querkrümmungshalbmesser nd
        final Double nd = c/Math.sqrt(1 + etasq);
        final Double nd2 = nd*nd;
        final Double nd4 = nd2*nd2;
        final Double nd6 = nd4*nd2;
        final Double nd3 = nd2*nd;
        final Double nd5 = nd4*nd;

        // Längendifferenz dl zum Bezugsmeridian lh
        final Double kzNR = rw/1e6;
        final Integer kzR = kzNR.intValue();
        final Double kz = kzR.doubleValue();
        final Double lh = kz*3;
        final Double dy = rw-(kz*1e6+500000);
        final Double dy2 = dy*dy;
        final Double dy4 = dy2*dy2;
        final Double dy3 = dy2*dy;
        final Double dy5 = dy4*dy;
        final Double dy6 = dy3*dy3;

        final Double b2 = -tan1*(1+etasq)/(2*nd2);
        final Double b4 =  tan1*(5+3*tan2+6*etasq*(1-tan2))/(24*nd4);
        final Double b6 = -tan1*(61+90*tan2+45*tan4)/(720*nd6);

        final Double l1 =  1/(nd*cos1);
        final Double l3 = -(1+2*tan2+etasq)/(6*nd3*cos1);
        final Double l5 =  (5+28*tan2+24*tan4)/(120*nd5*cos1);

        // Geographischer Breite bp und Länge lp als Funktion von Rechts- und Hochwert
        final Double bp = bf + (180/pi) * (b2*dy2 + b4*dy4 + b6*dy6);
        final Double lp = lh + (180/pi) * (l1*dy  + l3*dy3 + l5*dy5);

        if (lp < 5 || lp > 16 || bp < 46 || bp > 56) {
            // RW und/oder HW ungültig für das deutsche Gauss-Krüger-System
            return Optional.empty();
        }

        return Optional.of(new GKCoordinate(lp, bp));
    }

    /// Die Funktion verschiebt das Kartenbezugssystem (map datum) vom in
    /// Deutschland gebräuchlichen Potsdam-Datum zum WGS84 (World Geodetic
    /// System 84) Datum. Geographische Länge lp und Breite bp gemessen in
    /// grad auf dem Bessel-Ellipsoid müssen gegeben sein.
    /// Ausgegeben werden geographische Länge lw und
    /// Breite bw (in grad) auf dem WGS84-Ellipsoid.
    /// Bei der Transformation werden die Ellipsoidachsen parallel
    /// verschoben um dx = 587 m, dy = 16 m und dz = 393 m.
    public static WGSCoordinate pot2wgs(GKCoordinate pot) {
        final Double lp = pot.getX();
        final Double bp = pot.getY();

        // Quellsystem Potsdam Datum
        // Große Halbachse a und Abplattung fq
        final Double a = 6378137.000 - 739.845;
        final Double fq = 3.35281066e-3 - 1.003748e-05;

        // Zielsystem WGS84 Datum
        // Abplattung f
        final Double f = 3.35281066e-3;

        // Parameter für datum shift
        final Double dx = 587.0;
        final Double dy = 16.0;
        final Double dz = 393.0;

        // Quadrat der ersten numerischen Exzentrizität in Quell- und Zielsystem
        final Double e2q = (2*fq-fq*fq);
        final Double e2 = (2*f-f*f);

        // Breite und Länge in Radianten
        final Double pi = Math.PI;
        final Double b1 = bp * (pi/180);
        final Double l1 = lp * (pi/180);

        // Querkrümmungshalbmesser nd
        final Double nd = a/Math.sqrt(1 - e2q*Math.sin(b1)*Math.sin(b1));

        // Kartesische Koordinaten des Quellsystems Potsdam
        final Double xp = nd*Math.cos(b1)*Math.cos(l1);
        final Double yp = nd*Math.cos(b1)*Math.sin(l1);
        final Double zp = (1 - e2q)*nd*Math.sin(b1);

        // Kartesische Koordinaten des Zielsystems (datum shift) WGS84
        final Double x = xp + dx;
        final Double y = yp + dy;
        final Double z = zp + dz;

        // Berechnung von Breite und Länge im Zielsystem
        final Double rb = Math.sqrt(x*x + y*y);
        final Double b2 = (180/pi) * Math.atan((z/rb)/(1-e2));

        Double l2 = 0.0;

        if (x > 0) {
            l2 = (180/pi) * Math.atan(y/x);
        }
        if (x < 0 && y > 0) {
            l2 = (180/pi) * Math.atan(y/x) + 180;
        }
        if (x < 0 && y < 0) {
            l2 = (180/pi) * Math.atan(y/x) - 180;
        }

        return new WGSCoordinate(b2, l2);
    }

    /// Die Funktion verschiebt das Kartenbezugssystem (map datum) vom
    /// WGS84 Datum (World Geodetic System 84) zum in Deutschland
    /// gebräuchlichen Potsdam-Datum. Geographische Länge lw und Breite
    /// bw gemessen in grad auf dem WGS84 Ellipsoid müssen
    /// gegeben sein. Ausgegeben werden geographische Länge lp
    /// und Breite bp (in grad) auf dem Bessel-Ellipsoid.
    /// Bei der Transformation werden die Ellipsoidachsen parallel
    /// verschoben um dx = -587 m, dy = -16 m und dz = -393 m.
    /// Fehler berichten Sie bitte an Helmut.Heimeier@t-online.de
    public static Optional<GKCoordinate> wgs2pot(WGSCoordinate wgs) {
        final Double lw = wgs.getLongitude();
        final Double bw = wgs.getLatitude();

        // Quellsystem WGS 84 Datum
        // Große Halbachse a und Abplattung fq
        final Double a = 6378137.000;
        final Double fq = 3.35281066e-3;

        // Zielsystem Potsdam Datum
        // Abplattung f
        final Double f = fq - 1.003748e-5;

        // Parameter für datum shift
        final Double dx = -587.0;
        final Double dy = -16.0;
        final Double dz = -393.0;

        // Quadrat der ersten numerischen Exzentrizität in Quell- und Zielsystem
        final Double e2q = (2*fq-fq*fq);
        final Double e2 = (2*f-f*f);

        // Breite und Länge in Radianten
        final Double pi = Math.PI;
        final Double b1 = bw * (pi/180);
        final Double l1 = lw * (pi/180);

        // Querkrümmungshalbmesser nd
        final Double nd = a/Math.sqrt(1 - e2q*Math.sin(b1)*Math.sin(b1));

        // Kartesische Koordinaten des Quellsystems WGS84
        final Double xw = nd*Math.cos(b1)*Math.cos(l1);
        final Double yw = nd*Math.cos(b1)*Math.sin(l1);
        final Double zw = (1 - e2q)*nd*Math.sin(b1);

        // Kartesische Koordinaten des Zielsystems (datum shift) Potsdam
        final Double x = xw + dx;
        final Double y = yw + dy;
        final Double z = zw + dz;

        // Berechnung von Breite und Länge im Zielsystem
        final Double rb = Math.sqrt(x*x + y*y);
        final Double b2 = (180/pi) * Math.atan((z/rb)/(1-e2));

        Double l2 = 0.0;
        if (x > 0) {
            l2 = (180/pi) * Math.atan(y/x);
        }
        if (x < 0 && y > 0) {
            l2 = (180/pi) * Math.atan(y/x) + 180;
        }
        if (x < 0 && y < 0) {
            l2 = (180/pi) * Math.atan(y/x) - 180;
        }

        if (l2 < 5 || l2 > 16 || b2 < 46 || b2 > 56) {
            return Optional.empty();
        }

        return Optional.of(new GKCoordinate(l2, b2));
    }

    /// Die Funktion wandelt geographische Koordinaten in GK Koordinaten
    /// um. Geographische Länge lp und Breite bp müssen im Potsdam Datum
    /// gegeben sein. Berechnet werden Rechtswert rw und Hochwert hw.
    /// Fehler berichten Sie bitte an Helmut.Heimeier@t-online.de
    public static Optional<GKCoordinate> pot2gk(GKCoordinate pot) {
        final Double lp = pot.getX();
        final Double bp = pot.getY();

        if (bp < 46 || bp > 56 || lp < 5 || lp > 16) {
            // Werte außerhalb des für Deutschland definierten Gauss-Krüger-Systems
            return Optional.empty();
        }

        // Potsdam Datum
        // Große Halbachse a und Abplattung f
        final Double a = 6377397.155;
        final Double f = 3.342773154e-3;
        final Double pi = Math.PI;

        // Polkrümmungshalbmesser c
        final Double c = a/(1-f);

        // Quadrat der zweiten numerischen Exzentrizität
        final Double ex2 = (2*f-f*f)/((1-f)*(1-f));
        final Double ex4 = ex2*ex2;
        final Double ex6 = ex4*ex2;
        final Double ex8 = ex4*ex4;

        // Koeffizienten zur Berechnung der Meridianbogenlänge
        final Double e0 = c*(pi/180)*(1 - 3*ex2/4 + 45*ex4/64  - 175*ex6/256  + 11025*ex8/16384);
        final Double e2 =            c*(  -3*ex2/8 + 15*ex4/32  - 525*ex6/1024 +  2205*ex8/4096);
        final Double e4 =                          c*(15*ex4/256 - 105*ex6/1024 +  2205*ex8/16384);
        final Double e6 =                                    c*( -35*ex6/3072 +   315*ex8/12288);

        // Breite in Radianten
        final Double br = bp * pi/180;

        final Double tan1 = Math.tan(br);
        final Double tan2 = tan1*tan1;
        final Double tan4 = tan2*tan2;

        final Double cos1 = Math.cos(br);
        final Double cos2 = cos1*cos1;
        final Double cos4 = cos2*cos2;
        final Double cos3 = cos2*cos1;
        final Double cos5 = cos4*cos1;

        final Double etasq = ex2*cos2;

        // Querkrümmungshalbmesser nd
        final Double nd = c/Math.sqrt(1 + etasq);

        // Meridianbogenlänge g aus gegebener geographischer Breite bp
        final Double g = e0*bp + e2*Math.sin(2*br) + e4*Math.sin(4*br) + e6*Math.sin(6*br);

        // Längendifferenz dl zum Bezugsmeridian lh
        final Double kzNR = (lp+1.5)/3;
        final Integer kzR = kzNR.intValue();
        final Double kz = kzR.doubleValue();
        final Double lh = kz*3;
        final Double dl = (lp - lh)*pi/180;
        final Double dl2 = dl*dl;
        final Double dl4 = dl2*dl2;
        final Double dl3 = dl2*dl;
        final Double dl5 = dl4*dl;

        // Hochwert hw und Rechtswert rw als Funktion von geographischer Breite und Länge
        Double hw =  (g + nd*cos2*tan1*dl2/2 + nd*cos4*tan1*(5-tan2+9*etasq)*dl4/24);
        Double rw =      (nd*cos1*dl         + nd*cos3*(1-tan2+etasq)*dl3/6 + nd*cos5*(5-18*tan2+tan4)*dl5/120 + kz*1e6 + 500000);

        Integer hwR = hw.intValue();
        Double hwRR = hwR.doubleValue();
        Double nk = hw - hwRR;
        hwR = hw.intValue();
        hwRR = hwR.doubleValue();

        if (nk < 0.5) {
            hw = hwRR;
        } else {
            hw = hwRR + 1;
        }

        Integer rwR = rw.intValue();
        Double rwRR = rwR.doubleValue();
        Double rk = rw - rwRR;
        rwR = rw.intValue();
        rwRR = rwR.doubleValue();

        if (nk < 0.5) {
            rw = rwRR;
        } else {
            rw = rwRR + 1;
        }

        return Optional.of(new GKCoordinate(rw, hw));
    }

}
