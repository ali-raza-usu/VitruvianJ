package vitruvianJ.delegate;

public interface IDelegate extends Subject{
    /**
     * Thin wrapper in invoke
     * @param args possibly null array or args - null says none
     * @return possibly null return - primitive types are wrapped
     */
     public Object invoke(Object[] args);
    /**
     * convenience call for 1 arg case
     * @param arg possibly null argument
     * @return possibly null return - primitive types are wrapped
     */
     public Object invoke(Object arg);

    /**
     * convenience call for 2 arg case
     * @param arg1 possibly null argument
     * @param arg2 possibly null argument
     * @return possibly null return - primitive types are wrapped
     */
     public Object invoke(Object arg1, Object arg2);

    /**
     * convenience call for no arg case
     * @return possibly null return - primitive types are wrapped
     */
     public Object invoke();
     
     
          
}