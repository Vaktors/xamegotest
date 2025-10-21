public class Caneta {
    private String modelo;
    private String cor;
    private float ponta;
    private boolean tampada = false;

    public Caneta(String m, String c, float p){
        this.modelo = m;
        this.cor = c;
        this.ponta = p;
        this.cor = "azul";
        this.tampar();
        
    }

    public String getmodelo(){
        return this.modelo;
    }
    public void setmodelo(String m){
        modelo = m;
    }

    public float getponta(){
        return this.ponta;
    }
    public void setponta(float p){
        ponta = p;
    }

    public void status(){
        System.out.println(" SOBRE A CANETA");
        System.out.println("modelo " + this.modelo);
        System.out.println("ponta " + this.ponta);
    }

    public void tampar(){
        this.tampada = true;    
    }
    public void destampar(){
        this.tampada = false;
    }
}
